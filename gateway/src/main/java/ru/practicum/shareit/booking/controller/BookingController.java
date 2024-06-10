package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody BookingDto bookingDto,
                                      @RequestHeader(HEADER) Long userId) {
        log.info("New POST request /bookings");
        return bookingClient.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approve(@PathVariable Long bookingId, @RequestParam(name = "approved") Boolean approved,
                                          @RequestHeader(HEADER) Long userId) {
        log.info("New PATCH request /bookings/{}", bookingId);
        return bookingClient.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId, @RequestHeader(HEADER) Long userId) {
        log.info("New GET request /bookings/{}", bookingId);
        return bookingClient.getById(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                               @RequestHeader(HEADER) Long bookerId,
                                               @RequestParam(defaultValue = "0")
                                               @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10")
                                               @Positive int size) {
        log.info("New GET request for all bookings for user {}", bookerId);
        return bookingClient.getAllByUser(bookerId, BookingState.getEnumByString(state), from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                                @RequestHeader(HEADER) Long ownerId,
                                                @RequestParam(defaultValue = "0")
                                                @PositiveOrZero int from,
                                                @RequestParam(defaultValue = "10")
                                                @Positive int size) {
        log.info("New GET request /bookings/owner?state= , ownerId = {}", ownerId);
        return bookingClient.getAllByOwner(ownerId, BookingState.getEnumByString(state), from, size);
    }
}
