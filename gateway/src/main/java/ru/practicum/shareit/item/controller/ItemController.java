package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemClient itemClient;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(HEADER) Long userId,
                                      @Validated({Marker.OnCreate.class}) @RequestBody ItemDto itemDto) {
        log.info("New request for item creation from userId={}", userId);
        return itemClient.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId, @RequestHeader(HEADER) Long userId,
                             @Validated({Marker.OnUpdate.class}) @RequestBody ItemDto itemDto) {
        log.info("New request for item update from userId={}", userId);
        return itemClient.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@PathVariable Long itemId, @RequestHeader(HEADER) Long userId) {
        log.info("New request for item search with id={}", itemId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(HEADER) Long userId,
                                               @RequestParam(defaultValue = "0")
                                               @PositiveOrZero int from,
                                               @RequestParam(defaultValue = "10")
                                               @Positive int size) {
        log.info("New request for user items with userId={}", userId);
        return itemClient.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestHeader(HEADER) Long userId, @RequestParam String text,
                                             @RequestParam(defaultValue = "0")
                                             @PositiveOrZero int from,
                                             @Positive @RequestParam(defaultValue = "10")
                                             int size) {
        log.info("New request for searching item by text={}", text);
        return itemClient.search(text,userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@PathVariable Long itemId,
                                    @RequestHeader(HEADER) Long userId,
                                    @Validated(Marker.OnCreate.class) @RequestBody CommentDto commentDto) {
        log.info("New request to create comment for item {}", itemId);
        return itemClient.addComment(itemId, userId, commentDto);
    }
}
