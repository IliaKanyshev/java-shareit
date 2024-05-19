package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.exception.OwnerException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {
        User user = validateAndGetUser(userId);
        itemDto.setOwner(user);
        return ItemMapper.toItemDto(itemStorage.add(ItemMapper.toItemFromDto(itemDto)));
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        validateAndGetUser(userId);
        Item item = itemStorage.get(itemId).orElseThrow(() -> new ItemNotFoundException("Item not found."));
        if (!item.getOwner().getId().equals(userId)) {
            throw new OwnerException("Only owner can change item properties.");
        }
        if (itemDto.getName() != null) item.setName(itemDto.getName());
        if (itemDto.getDescription() != null) item.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) item.setAvailable(itemDto.getAvailable());
        return ItemMapper.toItemDto(itemStorage.update(itemId, item));
    }

    @Override
    public ItemDto get(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.get(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Item with id %d not found.", itemId))));
    }

    @Override
    public Collection<ItemDto> getUserItems(Long userId) {
        validateAndGetUser(userId);
        return itemStorage.getUserItems(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public Collection<ItemDto> searchItem(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.searchItem(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private User validateAndGetUser(Long userId) {
        return userStorage.get(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
    }
}
