package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingOwnerException;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static ru.practicum.shareit.util.Status.WAITING;


@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BookingServiceImpl implements BookingService {
    private final BookingStorage bookingStorage;
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public BookingDtoOut add(BookingDto bookingDto, Long userId) {
        User user = getUser(userId);
        Item item = getItem(bookingDto.getItemId());
        if (item.getOwner().getId().equals(userId)) {
            throw new BookingOwnerException("User can't book own item.");
        }
        if (!item.getAvailable()) {
            throw new BadRequestException("User can't book unavailable items.");
        }
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        bookingStorage.save(booking);
        log.info("Booking was created.");
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDtoOut getById(Long bookingId, Long userId) {
        Booking booking = getBookingById(bookingId);
        User booker = booking.getBooker();
        User owner = getUser(booking.getItem().getOwner().getId());
        if (!Objects.equals(booker.getId(), userId) && !Objects.equals(owner.getId(), userId)) {
            throw new BookingOwnerException("Only owner or booker can get booking info.");
        }
        log.info("Getting booking with id {}", bookingId);
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public BookingDtoOut approve(Long bookingId, Long userId, Boolean approved) {
        User owner = getUser(userId);
        Booking booking = getBookingById(bookingId);
        Item item = getItem(booking.getItem().getId());
        if (!owner.getId().equals(item.getOwner().getId())) {
            throw new BookingOwnerException("Only owner can approve or reject.");
        }
        if (!booking.getStatus().equals(WAITING)) {
            throw new BadRequestException("Only WAITING can be approved or rejected");
        }
        Status status = approved ? Status.APPROVED : Status.REJECTED;
        booking.setStatus(status);
        log.info("Booking status of booking with id {} was updated. ", booking.getId());
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDtoOut> getAllByUser(Long bookerId, Status status) {
        User booker = getUser(bookerId);
        List<Booking> bookings;
        switch (status) {
            case ALL:
                bookings = bookingStorage.findAllByBookerId(booker.getId(), Sort.by(DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByBookerIdAndStateCurrent(booker.getId(),
                        Sort.by(DESC, "start"));
                break;
            case PAST:
                bookings = bookingStorage.findAllByBookerIdAndStatePast(booker.getId(),
                        Sort.by(DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByBookerIdAndStateFuture(booker.getId(),
                        Sort.by(DESC, "start"));
                break;
            case WAITING:
                bookings = bookingStorage.findAllByBookerIdAndStatus(booker.getId(),
                        Status.WAITING, Sort.by(DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByBookerIdAndStatus(booker.getId(),
                        Status.REJECTED, Sort.by(DESC, "end"));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<BookingDtoOut> getAllByOwner(Long ownerId, Status status) {
        User owner = getUser(ownerId);
        List<Booking> bookings;
        switch (status) {
            case ALL:
                bookings = bookingStorage.findAllByOwnerId(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingStorage.findAllByOwnerIdAndStateCurrent(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingStorage.findAllByOwnerIdAndStatePast(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingStorage.findAllByOwnerIdAndStateFuture(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingStorage.findAllByOwnerIdAndStatus(owner.getId(),
                        Status.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingStorage.findAllByOwnerIdAndStatus(owner.getId(),
                        Status.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    public Booking getBookingById(Long bookingId) {
        log.info("Getting booking with Id {}", bookingId);
        return bookingStorage.findById(bookingId).orElseThrow(() ->
                new BookingNotFoundException(String.format("Booking with id %d not found.", bookingId)));
    }

    private User getUser(Long userId) {
        return userStorage.findById(userId).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id %d not found.", userId)));
    }

    private Item getItem(Long itemId) {
        return itemStorage.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException(String.format("Item with id %d not found.", itemId)));
    }
}
