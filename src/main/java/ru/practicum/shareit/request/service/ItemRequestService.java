package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOut add(Long userId, ItemRequestDto itemRequestDto);
    ItemRequestDtoOut getById(Long userId, Long requestId);
    List<ItemRequestDtoOut> getAllByUserId(Long userId);
    List<ItemRequestDtoOut> getAll(Long userId, int from, int size);
}
