package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.Status;

import java.util.Collection;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOut add(@RequestBody BookingDto bookingDto,
                             @RequestHeader(HEADER) Long userId) {
        log.info("New POST request /bookings");
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approve(@PathVariable Long bookingId, @RequestParam(name = "approved") Boolean approved,
                                 @RequestHeader(HEADER) Long userId) {
        log.info("New PATCH request /bookings/{}", bookingId);
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@PathVariable Long bookingId, @RequestHeader(HEADER) Long userId) {
        log.info("New GET request /bookings/{}", bookingId);
        return bookingService.getById(bookingId, userId);
    }

    @GetMapping
    public Collection<BookingDtoOut> getAllByUser(@RequestParam(name = "state", defaultValue = "ALL") Status state,
                                                  @RequestHeader(HEADER) Long bookerId,
                                                  @RequestParam(defaultValue = "0") int from,
                                                  @RequestParam(defaultValue = "10") int size) {
        log.info("New GET request for all bookings for user {}", bookerId);
        return bookingService.getAllByUser(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public Collection<BookingDtoOut> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL") Status state,
                                                   @RequestHeader(HEADER) Long ownerId,
                                                   @RequestParam(defaultValue = "0") int from,
                                                   @RequestParam(defaultValue = "10") int size) {
        log.info("New GET request /bookings/owner?state= , ownerId = {}", ownerId);
        return bookingService.getAllByOwner(ownerId, state, from, size);
    }
}
