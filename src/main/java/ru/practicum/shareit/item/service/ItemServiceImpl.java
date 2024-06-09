package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;
import ru.practicum.shareit.util.exception.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.util.Status.APPROVED;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final CommentStorage commentStorage;
    private final BookingStorage bookingStorage;
    private final ItemRequestStorage itemRequestStorage;

    @Override
    public ItemDtoOut add(Long userId, ItemDto itemDto) {
        log.info("New item creation {}", itemDto.getName());
        User user = validateAndGetUser(userId);
        ItemRequest request = null;
        if (itemDto.getRequestId() != null) {
            request = itemRequestStorage.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new RequestNotFoundException("Request not found"));
        }
        Item item = ItemMapper.toItemFromDto(itemDto, request);
        item.setOwner(user);
        return ItemMapper.toItemDtoOut(itemStorage.save(item));
    }

    @Override
    public ItemDtoOut update(Long itemId, Long userId, ItemDto itemDto) {
        log.info("Item {} update", itemId);
        validateAndGetUser(userId);
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found."));
        if (!item.getOwner().getId().equals(userId)) {
            throw new OwnerException("Only owner can change item properties.");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDtoOut(itemStorage.save(item));
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDtoOut getById(Long itemId, Long userId) {
        log.info("Getting item {}", itemId);
        return itemStorage.findById(itemId).map(item -> addBookingsAndComments(userId, item)).orElseThrow(() ->
                new ItemNotFoundException(String.format(String.format("Item with id %d not found.", itemId))));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDtoOut> getUserItems(Long userId, int from, int size) {
        log.info("Get user {} items.", userId);
        validateAndGetUser(userId);
        List<Item> items = new ArrayList<>(itemStorage.findAllByOwnerId(userId, PageRequest.of(from / size, size)));
        return addBookingsAndCommentsList(items);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ItemDtoOut> search(String text, int from, int size) {
        log.info("Getting item by text {}", text);
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text.toLowerCase(), PageRequest.of(from / size, size)).stream().map(ItemMapper::toItemDtoOut).collect(toList());
    }

    @Override
    public CommentDtoOut addComment(Long itemId, Long userId, CommentDto commentDto) {
        User user = validateAndGetUser(userId);
        Item item = itemStorage.findById(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found."));
        if (bookingStorage.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId, itemId, APPROVED,
                LocalDateTime.now()).isEmpty()) {
            throw new BadRequestException("Comment can be added only after booking.");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        commentStorage.save(comment);
        return CommentMapper.toCommentDtoOut(comment);
    }

    private List<ItemDtoOut> addBookingsAndCommentsList(List<Item> items) {
        Map<Item, Booking> itemsWithLastBookings = bookingStorage
                .findByItemInAndStartLessThanEqualAndStatus(items, LocalDateTime.now(),
                        Status.APPROVED, Sort.by(DESC, "end"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity()));

        Map<Item, Booking> itemsWithNextBookings = bookingStorage
                .findByItemInAndStartAfterAndStatus(items, LocalDateTime.now(),
                        Status.APPROVED, Sort.by(ASC, "end"))
                .stream()
                .collect(Collectors.toMap(Booking::getItem, Function.identity(), (o1, o2) -> o1));

        Map<Item, List<Comment>> itemsWithComments = commentStorage
                .findByItemIn(items, Sort.by(DESC, "created"))
                .stream()
                .collect(groupingBy(Comment::getItem, toList()));

        List<ItemDtoOut> itemsDtoOutList = new ArrayList<>();
        for (Item item : items) {
            ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
            Booking lastBooking = itemsWithLastBookings.get(item);
            if (!itemsWithLastBookings.isEmpty() && lastBooking != null) {
                itemDtoOut.setLastBooking(BookingMapper.toBookingDtoShort(lastBooking));
            }
            Booking nextBooking = itemsWithNextBookings.get(item);
            if (!itemsWithNextBookings.isEmpty() && nextBooking != null) {
                itemDtoOut.setNextBooking(BookingMapper.toBookingDtoShort(nextBooking));
            }
            List<CommentDtoOut> commentDtoOutList = itemsWithComments.getOrDefault(item, Collections.emptyList())
                    .stream()
                    .map(CommentMapper::toCommentDtoOut)
                    .collect(toList());
            itemDtoOut.setComments(commentDtoOutList);

            itemsDtoOutList.add(itemDtoOut);
        }
        return itemsDtoOutList;
    }

    private ItemDtoOut addBookingsAndComments(Long userId, Item item) {
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
        if (Objects.equals(itemDtoOut.getOwner().getId(), userId)) {
            itemDtoOut.setLastBooking(bookingStorage
                    .findFirstByItemIdAndStartLessThanEqualAndStatus(itemDtoOut.getId(), LocalDateTime.now(),
                            Status.APPROVED, Sort.by(DESC, "end"))
                    .map(BookingMapper::toBookingDtoShort)
                    .orElse(null));

            itemDtoOut.setNextBooking(bookingStorage
                    .findFirstByItemIdAndStartAfterAndStatus(itemDtoOut.getId(), LocalDateTime.now(),
                            Status.APPROVED, Sort.by(ASC, "end"))
                    .map(BookingMapper::toBookingDtoShort)
                    .orElse(null));
        }

        itemDtoOut.setComments(commentStorage.findAllByItemId(itemDtoOut.getId())
                .stream()
                .map(CommentMapper::toCommentDtoOut)
                .collect(toList()));

        return itemDtoOut;
    }

    private User validateAndGetUser(Long userId) {
        return userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
    }
}
