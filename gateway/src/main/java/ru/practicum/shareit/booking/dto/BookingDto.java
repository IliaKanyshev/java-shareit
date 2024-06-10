package ru.practicum.shareit.booking.dto;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.util.EndStartDateConstraint;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
@EndStartDateConstraint
public class BookingDto {
    private Long id;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
    @NotNull
    private Long itemId;
}
