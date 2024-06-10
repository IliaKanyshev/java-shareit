package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.RequestNotFoundException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceTest {
    @Mock
    ItemStorage itemStorage;
    @Mock
    ItemRequestStorage requestStorage;
    @Mock
    UserStorage userStorage;
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;
    User user;
    ItemRequestDto itemRequestDto;
    Item item;

    @BeforeEach
    void init() {
        user = new User(
                1L,
                "name",
                "email@email.ru");
        itemRequestDto = new ItemRequestDto(
                "description",
                LocalDateTime.now());
        item = new Item(
                1L,
                "name",
                "description",
                true,
                user,
                null);
    }

    @Test
    void createUserFoundTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(requestStorage.save(any())).thenReturn(itemRequest);
        ItemRequestDtoOut actual = itemRequestService.add(1L, itemRequestDto);
        itemRequestDto.setCreated(actual.getCreated());
        verify(requestStorage, Mockito.times(1)).save(any());
    }

    @Test
    void createUserNotFoundTest() {
        when(userStorage.findById(anyLong())).thenThrow(new UserNotFoundException("User not found"));
        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> itemRequestService.add(1L, itemRequestDto));
        assertEquals("User not found", e.getMessage());
    }

    @Test
    void getRequestsListUserNotFoundTest() {
        when(userStorage.findById(anyLong())).thenThrow(new UserNotFoundException("User not found"));
        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> itemRequestService.getAllByUserId(1L));
        assertEquals("User not found", e.getMessage());
    }

    @Test
    void getRequestInfoTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        when(requestStorage.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        item.setItemRequest(itemRequest);
        when(itemStorage.findByItemRequestId(anyLong())).thenReturn(Collections.singletonList(item));
        ItemRequestDtoOut responseRequest = itemRequestService.getById(user.getId(), 1L);
        assertNotNull(responseRequest);
        verify(requestStorage).findById(anyLong());
        verify(itemStorage).findByItemRequestId(anyLong());
    }

    @Test
    void getRequestByIdRequestNotFoundTest() {
        when(userStorage.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestStorage.findById(anyLong())).thenThrow(new RequestNotFoundException("Request not found"));
        RequestNotFoundException e = assertThrows(RequestNotFoundException.class, () ->
                itemRequestService.getById(user.getId(), 1L));
        assertEquals("Request not found", e.getMessage());
    }
}
