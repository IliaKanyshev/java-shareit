package ru.practicum.shareit.util;

import ru.practicum.shareit.booking.dto.BookingDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EndStartDateValidator implements ConstraintValidator<EndStartDateConstraint, BookingDto> {
    @Override
    public boolean isValid(BookingDto bookingDto,
                           ConstraintValidatorContext constraintValidatorContext) {
        return bookingDto.getStart() != null &&
                bookingDto.getEnd() != null &&
                bookingDto.getStart().isBefore(bookingDto.getEnd());
    }
}
