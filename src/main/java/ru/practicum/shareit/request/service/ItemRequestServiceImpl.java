package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.RequestNotFoundException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Override
    public ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto) {
        User user = getUser(userId);
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest request = itemRequestStorage.save(ItemRequestMapper.toItemRequest(itemRequestDto, user));
        log.info("User with id {} was created.", userId);
        return ItemRequestMapper.toItemRequestDtoOut(request);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDtoOut getById(Long userId, Long requestId) {
        getUser(userId);
        ItemRequest request = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Request %d not found", requestId)));
        ItemRequestDtoOut requestDtoOut = ItemRequestMapper.toItemRequestDtoOut(request);
        List<ItemDtoOut> items = itemStorage.findByItemRequestId(requestId).stream()
                .map(ItemMapper::toItemDtoOut)
                .collect(Collectors.toList());
        requestDtoOut.setItems(items);
        log.info("Item request with id {} loaded.", requestId);
        return requestDtoOut;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoOut> getAllByUserId(Long userId) {
        getUser(userId);
        List<ItemRequestDtoOut> result = itemRequestStorage.findAllByRequesterIdOrderByCreatedAsc(userId).stream()
                .map(ItemRequestMapper::toItemRequestDtoOut)
                .collect(Collectors.toList());
        addRequestsItems(result);
        log.info("Item requests list by user id {}", userId);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDtoOut> getAll(Long userId, int from, int size) {
        getUser(userId);
        List<ItemRequestDtoOut> result = itemRequestStorage
                .findAllByRequesterIdNotLikeOrderByCreatedAsc(userId, PageRequest.of(from / size, size)).stream()
                .map(ItemRequestMapper::toItemRequestDtoOut).collect(Collectors.toList());
        addRequestsItems(result);
        log.info("Item requests list pageable:");
        return result;
    }

    private User getUser(Long userId) {
        return userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User %d not found", userId)));
    }

    private void addRequestsItems(List<ItemRequestDtoOut> list) {
        Map<Long, ItemRequestDtoOut> requests = list.stream()
                .collect(Collectors.toMap(ItemRequestDtoOut::getId, o -> o, (a, b) -> b)); //functionIdentity - a,b
        List<Long> ids = requests.values().stream()
                .map(ItemRequestDtoOut::getId)
                .collect(Collectors.toList());
        List<ItemDtoOut> items = itemStorage.searchByRequestIds(ids).stream()
                .map(ItemMapper::toItemDtoOut)
                .collect(Collectors.toList());
        items.forEach(itemDto -> requests.get(itemDto.getRequestId()).getItems().add(itemDto));
    }
}
