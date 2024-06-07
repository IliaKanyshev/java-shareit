package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemRequestController {
    private static final String HEADER = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoOut add(@RequestHeader(HEADER) Long userId, @Validated(Marker.OnCreate.class) @RequestBody ItemRequestDto itemRequestDto
    ) {
        log.info("New POST /requests request");
        return itemRequestService.add(userId, itemRequestDto);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoOut getRequest(@RequestHeader(HEADER) Long userId, @PathVariable Long requestId) {
        log.info("New GET requests/{requestId} request");
        return itemRequestService.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoOut> getAll(@RequestHeader(HEADER) Long userId,
                                          @RequestParam(defaultValue = "0")
                                          @Min(value = 0) int from,
                                          @RequestParam(defaultValue = "10")
                                          @Min(value = 1) int size) {
        log.info("New GET requests/all request");
        return itemRequestService.getAll(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDtoOut> getAllByUserId(@RequestHeader(HEADER) Long userId) {
        log.info("New GET /requests request");
        return itemRequestService.getAllByUserId(userId);
    }

}
