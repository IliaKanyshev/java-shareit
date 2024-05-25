package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto add(Long userId, ItemDto itemDto);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    ItemDto get(Long itemId);

    Collection<ItemDto> getUserItems(Long userId);

    Collection<ItemDto> searchItem(String text);
}
