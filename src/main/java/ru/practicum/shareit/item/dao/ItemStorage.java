package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Optional;

public interface ItemStorage {
    Item add(Item item);

    Item update(Long itemId, Item item);

    // void delete(Long userId, Long itemId);
    Optional<Item> get(Long id);

    Collection<Item> getUserItems(Long userId);

    Collection<Item> searchItem(String text);
}
