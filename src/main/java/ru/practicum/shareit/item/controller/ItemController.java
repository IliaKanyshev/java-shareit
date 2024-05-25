package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.Marker;

import java.util.Collection;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto add(@RequestHeader(HEADER) Long userId,
                       @Validated({Marker.OnCreate.class}) @RequestBody ItemDto itemDto) {
        log.info("New request for item creation from userId={}", userId);
        return itemService.add(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId, @RequestHeader(HEADER) Long userId,
                          @Validated({Marker.OnUpdate.class}) @RequestBody ItemDto itemDto) {
        log.info("New request for item update from userId={}", userId);
        return itemService.update(itemId, userId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.info("New request for item search with id={}", itemId);
        return itemService.get(itemId);
    }

    @GetMapping
    public Collection<ItemDto> getUserItems(@RequestHeader(HEADER) Long userId) {
        log.info("New request for user items with userId={}", userId);
        return itemService.getUserItems(userId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text) {
        log.info("New request for searching item by text={}", text);
        return itemService.searchItem(text);
    }
}
