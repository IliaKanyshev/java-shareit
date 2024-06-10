package ru.practicum.shareit.util;

import ru.practicum.shareit.util.exception.BadRequestException;

public enum Status {

    WAITING,
    APPROVED,
    REJECTED,
    ALL,
    CURRENT,
    PAST,
    FUTURE,
    CANCELED;

    public static Status getEnumByString(String value) {
        Status status;
        try {
            status = Status.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + value);
        }
        return status;
    }
}
