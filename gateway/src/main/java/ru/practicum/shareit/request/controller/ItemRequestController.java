package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(HEADER) Long userId, @Validated(Marker.OnCreate.class) @RequestBody ItemRequestDto itemRequestDto
    ) {
        log.info("New POST /requests request");
        return itemRequestClient.add(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(HEADER) Long userId, @PathVariable Long requestId) {
        log.info("New GET requests/{requestId} request");
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader(HEADER) Long userId,
                                         @RequestParam(defaultValue = "0")
                                         @PositiveOrZero int from,
                                         @RequestParam(defaultValue = "10")
                                         @Positive int size) {
        log.info("New GET requests/all request");
        return itemRequestClient.getAll(userId, from, size);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUserId(@RequestHeader(HEADER) Long userId) {
        log.info("New GET /requests request");
        return itemRequestClient.getAllByUserId(userId);
    }

}
