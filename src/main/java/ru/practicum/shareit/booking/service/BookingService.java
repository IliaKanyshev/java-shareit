package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.util.Status;

import java.util.Collection;

public interface BookingService {
    BookingDtoOut add(BookingDto bookingDto, Long userId);

    BookingDtoOut getById(Long userId, Long bookingId);

    BookingDtoOut approve(Long userId, Long bookingId, Boolean approved);

    Collection<BookingDtoOut> getAllByUser(Long bookerId, Status status, int from, int size);

    Collection<BookingDtoOut> getAllByOwner(Long ownerId, Status status, int from, int size);
}
