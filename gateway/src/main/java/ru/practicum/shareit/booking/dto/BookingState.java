package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.util.exception.BadRequestException;

public enum BookingState {

    WAITING,
    APPROVED,
    REJECTED,
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    CANCELED;

    public static BookingState getEnumByString(String value) {
        BookingState status;
        try {
            status = BookingState.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + value);
        }
        return status;
    }
}
