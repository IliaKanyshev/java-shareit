package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class BookingDtoShort {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private Long bookerId;
}
