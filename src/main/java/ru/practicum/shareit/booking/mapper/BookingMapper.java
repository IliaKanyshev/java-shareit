package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.util.Status;

public class BookingMapper {
    public static Booking toBooking(BookingDto bookingDto) {
        return Booking.builder()
                // .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .status(Status.WAITING)
                .build();
    }

    public static BookingDtoOut toBookingDtoOut(Booking booking) {
        return BookingDtoOut.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDtoShort(booking.getItem()))
                .booker(UserMapper.toUserDtoShort(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingDtoShort toBookingDtoShort(Booking booking) {
        return BookingDtoShort.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
