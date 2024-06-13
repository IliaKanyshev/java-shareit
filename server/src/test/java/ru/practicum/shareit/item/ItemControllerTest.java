package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.exception.BadRequestException;
import ru.practicum.shareit.util.exception.ItemNotFoundException;
import ru.practicum.shareit.util.exception.RequestNotFoundException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {
    @Autowired
    private ItemController itemController;

    @Autowired
    private UserController userController;

    @Autowired
    private BookingController bookingController;

    @Autowired
    private ItemRequestController itemRequestController;

    private ItemDto itemDto;

    private ItemDto itemDto2;

    private UserDto userDto;

    private UserDto userDto2;

    private ItemRequestDto itemRequestDto;

    private CommentDto comment;

    @BeforeEach
    void init() {
        itemDto = ItemDto
                .builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        itemDto2 = ItemDto
                .builder()
                .name("new name")
                .description("new description")
                .available(true)
                .build();

        userDto = UserDto
                .builder()
                .name("name")
                .email("user@email.com")
                .build();

        userDto2 = UserDto
                .builder()
                .name("name")
                .email("user2@email.com")
                .build();

        itemRequestDto = ItemRequestDto
                .builder()
                .description("item request description")
                .build();

        comment = CommentDto
                .builder()
                .text("first comment")
                .build();
    }

    @Test
    void createTest() {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(1L, itemDto);
        assertEquals(item.getId(), itemController.get(item.getId(), user.getId()).getId());
    }

    @Test
    void createByWrongUser() {
        assertThrows(UserNotFoundException.class, () -> itemController.add(1L, itemDto));
    }

    @Test
    void createWithWrongItemRequest() {
        itemDto.setRequestId(10L);
        UserDto user = userController.add(userDto);
        assertThrows(RequestNotFoundException.class, () -> itemController.add(1L, itemDto));
    }

    @Test
    void updateTest() {
        userController.add(userDto);
        itemController.add(1L, itemDto);
        itemController.update(1L, 1L, itemDto2);
        assertEquals(itemDto2.getDescription(), itemController.get(1L, 1L).getDescription());
    }

    @Test
    void updateForWrongItemTest() {
        assertThrows(UserNotFoundException.class, () -> itemController.update(1L, 1L, itemDto));
    }

    @Test
    void updateByWrongUserTest() {
        userController.add(userDto);
        itemController.add(1L, itemDto);
        assertThrows(ItemNotFoundException.class, () -> itemController.update(10L, 1L, itemDto2));
    }

    @Test
    void searchTest() {
        userController.add(userDto);
        itemController.add(1L, itemDto);
        assertEquals(1, itemController.searchItem("Desc", 0, 10).size());
    }

    @Test
    void searchEmptyTextTest() {
        userController.add(userDto);
        itemController.add(1L, itemDto);
        assertEquals(new ArrayList<ItemDtoOut>(), itemController.searchItem("", 0, 10));
    }

    @Test
    void createCommentTest() throws InterruptedException {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        UserDto user2 = userController.add(userDto2);
        bookingController.add(BookingDto
                .builder()
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now().plusSeconds(2))
                .itemId(item.getId()).build(), user2.getId());
        bookingController.approve(1L, true, user.getId());
        TimeUnit.SECONDS.sleep(2);
        itemController.addComment(item.getId(), user2.getId(), comment);
        assertEquals(1, itemController.get(1L, 1L).getComments().size());
    }

    @Test
    void createCommentByWrongUser() {
        assertThrows(UserNotFoundException.class, () -> itemController.addComment(1L, 1L, comment));
    }

    @Test
    void createCommentToWrongItem() {
        UserDto user = userController.add(userDto);
        assertThrows(ItemNotFoundException.class, () -> itemController.addComment(1L, 1L, comment));
        ItemDtoOut item = itemController.add(1L, itemDto);
        assertThrows(BadRequestException.class, () -> itemController.addComment(1L, 1L, comment));
    }
}
