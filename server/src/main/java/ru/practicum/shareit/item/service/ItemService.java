package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.Collection;

public interface ItemService {
    ItemDtoOut add(Long userId, ItemDto itemDto);

    ItemDtoOut update(Long itemId, Long userId, ItemDto itemDto);

    ItemDtoOut getById(Long itemId, Long userId);

    Collection<ItemDtoOut> getUserItems(Long userId, int from, int size);

    Collection<ItemDtoOut> search(String text, int from, int size);

    CommentDtoOut addComment(Long itemId, Long userId, CommentDto commentDto);

}
