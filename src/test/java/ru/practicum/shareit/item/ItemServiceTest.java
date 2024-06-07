package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.exception.BadRequestException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;

    @Mock
    private BookingStorage bookingStorage;
    @Mock
    private CommentStorage commentStorage;
    @Mock
    private ItemRequestStorage itemRequestStorage;

    @InjectMocks
    private ItemServiceImpl itemService;
    ItemDto itemDto;
    User user;
    Item item;
    ItemDtoOut itemDtoOut;

    @BeforeEach
    void init() {
        user = new User(1L, "user", "user@mail.ru");
        item = new Item(1L, "item", "description", true, user, null);
        itemDto = ItemMapper.toItemDto(item);
        itemDtoOut = ItemMapper.toItemDtoOut(item);
    }

    @Test
    @SneakyThrows
    void addItemTest() {
        when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemStorage.save(any()))
                .thenReturn(item);

        assertEquals(itemDto.getName(), itemService.add(1L, itemDto).getName());
    }

    @Test
    @SneakyThrows
    void addItemNoUserTest() {
        ItemDto itemDto1 = new ItemDto(1L, "item", "desc", true, 0L);
        assertThrows(UserNotFoundException.class, () -> itemService.add(1L, itemDto1));
    }

    @Test
    void findAllTest() {
        when(userStorage.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        when(itemStorage.findAllByOwnerId(anyLong(), any()))
                .thenReturn(Collections.emptyList());
        assertTrue(itemService.getUserItems(1L, 0, 10).isEmpty());
    }

    @Test
    @SneakyThrows
    void getItemItemFoundTest() {
        when(itemStorage.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        ItemDtoOut actualItem = itemService.getById(item.getId(), user.getId());
        assertNotNull(actualItem);
        assertEquals(item.getName(), actualItem.getName());
    }

    @Test
    void getItemItemNotFoundTest() {
        when(itemStorage.findById(1L)).thenReturn(Optional.empty());
        ItemNotFoundException e = assertThrows(ItemNotFoundException.class, () -> itemService.getById(1L, 1L));
        assertEquals("Item with id 1 not found.", e.getMessage());
    }

    @Test
    void addItemUserNotFoundTest() {
        when(userStorage.findById(1L)).thenReturn(Optional.empty());
        UserNotFoundException e = assertThrows(UserNotFoundException.class, () -> itemService.add(1L, new ItemDto()));
        assertEquals("User not found.", e.getMessage());
    }


    @Test
    void updateOwnerExceptionTest() {
        User user = new User(
                1L,
                "name",
                "email@mail.ru");
        User user2 = new User(
                2L,
                "name",
                "email@mail.ru");
        Item item = new Item(
                1L,
                "name",
                "description",
                true,
                user,
                null);

        UserNotFoundException e = assertThrows(UserNotFoundException.class, () ->
                itemService.update(item.getId(), user.getId(), new ItemDto()));
        assertEquals("User not found.", e.getMessage());
    }

    @Test
    void searchItemTextNotBlankTest() {
        User user = new User(
                1L,
                "name",
                "email@mail.ru");
        Item item = new Item(
                1L,
                "name",
                "Какая-то дрель",
                true,
                user,
                null);
        when(itemStorage.search(anyString(), any())).thenReturn(Collections.singletonList(item));
        List<ItemDtoOut> actual = (List<ItemDtoOut>) itemService.search("дрель", 0, 10);
        assertEquals(1, actual.size());
        assertEquals(ItemMapper.toItemDtoOut(item), actual.get(0));
    }

    @Test
    void searchTextIsBlankTest() {
        when(itemStorage.search(anyString(), any())).thenReturn(Collections.emptyList());
        List<ItemDtoOut> actual = (List<ItemDtoOut>) itemService.search("дрель", 0, 10);
        assertTrue(actual.isEmpty());
    }

    @Test
    @SneakyThrows
    void addComment() {
        ItemDto itemDto = new ItemDto(1L, "item", "desc", true, null);
        Item item = ItemMapper.toItemFromDto(itemDto, null);
        User user1 = new User(1L, "test", "test@mail.ru");
        CommentDto commentDto = new CommentDto();
        commentDto.setText("test");
        Booking booking = new Booking(1L, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(1), item, user1, Status.APPROVED);
        when(bookingStorage.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(itemStorage.findById(1L)).thenReturn(Optional.of(item));
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(commentStorage.save(any()))
                .thenReturn(CommentMapper.toComment(commentDto, item, user1));
        assertEquals(commentDto.getText(), itemService.addComment(1L, 1L, commentDto).getText());
    }

    @Test
    @SneakyThrows
    void addCommentNoBooking() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("test");
        new Booking(1L, LocalDateTime.MIN, LocalDateTime.MIN.plusHours(1), item, new User(1L, "test", "test@test.com"), Status.APPROVED);
        when(userStorage.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemStorage.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingStorage.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(anyLong(), anyLong(), any(), any()))
                .thenReturn(List.of());
        assertThrows(BadRequestException.class, () -> itemService.addComment(1L, 1L, commentDto));
    }
}
