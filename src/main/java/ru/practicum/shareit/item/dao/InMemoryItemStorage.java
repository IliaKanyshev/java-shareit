package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items;
    private Long counter = 0L;

    @Override
    public Item add(Item item) {
        item.setId(++counter);
        items.put(item.getId(), item);
        log.info("Item {} added.", item.getName());
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        items.put(itemId, item);
        log.info("Item {} was updated.", item.getName());
        return item;
    }

    @Override
    public Optional<Item> get(Long id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public Collection<Item> getUserItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Item> searchItem(String text) {
        return items.values().stream()
                .filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                        || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .collect(Collectors.toList());
    }
}
