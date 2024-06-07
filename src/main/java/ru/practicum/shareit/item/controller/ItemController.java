package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collection;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDtoOut add(@RequestHeader(HEADER) Long userId,
                          @Validated({Marker.OnCreate.class}) @RequestBody ItemDto itemDto) {
        log.info("New request for item creation from userId={}", userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut update(@PathVariable Long itemId, @RequestHeader(HEADER) Long userId,
                             @Validated({Marker.OnUpdate.class}) @RequestBody ItemDto itemDto) {
        log.info("New request for item update from userId={}", userId);
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut get(@PathVariable Long itemId, @RequestHeader(HEADER) Long userId) {
        log.info("New request for item search with id={}", itemId);
        return itemService.getById(itemId, userId);
    }

    @GetMapping
    public Collection<ItemDtoOut> getUserItems(@RequestHeader(HEADER) Long userId,
                                               @RequestParam(defaultValue = "0")
                                               @Min(value = 0) int from,
                                                @RequestParam(defaultValue = "10")
                                                   @Min(value = 1) int size) {
        log.info("New request for user items with userId={}", userId);
        return itemService.getUserItems(userId, from, size);
    }

    @GetMapping("/search")
    public Collection<ItemDtoOut> searchItem(@RequestParam String text,
                                              @RequestParam(defaultValue = "0")
                                              @Min(value = 0) int from,
                                             @Positive @RequestParam(defaultValue = "10")
                                                 @Min(value = 1) int size) {
        log.info("New request for searching item by text={}", text);
        return itemService.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(@PathVariable Long itemId,
                                    @RequestHeader(HEADER) Long userId,
                                    @Validated(Marker.OnCreate.class) @RequestBody CommentDto commentDto) {
        log.info("New request to create comment for item {}", itemId);
        return itemService.addComment(itemId, userId, commentDto);
    }
}
