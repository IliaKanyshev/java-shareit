package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemControllerTest {
    @Autowired
    private ItemRequestController itemRequestController;

    @Autowired
    private UserController userController;

    private ItemRequestDto itemRequestDto;

    private UserDto userDto;

    private UserDto userDto2;

    @BeforeEach
    void init() {
        itemRequestDto = ItemRequestDto
                .builder()
                .description("item request description")
                .build();

        userDto = UserDto
                .builder()
                .name("name")
                .email("user@email.com")
                .build();

        userDto2 = UserDto
                .builder()
                .name("name2")
                .email("user2@email.com")
                .build();
    }

    @Test
    void createTest() {
        UserDto user = userController.add(userDto);
        ItemRequestDtoOut itemRequest = itemRequestController.add(user.getId(), itemRequestDto);
        assertEquals(1L, itemRequestController.getRequest(itemRequest.getId(), user.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(UserNotFoundException.class, () -> itemRequestController.add(1L, itemRequestDto));
    }

    @Test
    void getAllByUserTest() {
        UserDto user = userController.add(userDto);
        ItemRequestDtoOut itemRequest = itemRequestController.add(user.getId(), itemRequestDto);
        assertEquals(1, itemRequestController.getAllByUserId(user.getId()).size());
    }

    @Test
    void getAllByUserWithWrongUserTest() {
        assertThrows(UserNotFoundException.class, () -> itemRequestController.getAllByUserId(1L));
    }

    @Test
    void getAllByWrongUser() {
        assertThrows(UserNotFoundException.class, () -> itemRequestController.getAll(0L, 0, 10));
    }

    @Test
    void getAllWithWrongFrom() {
        assertThrows(UserNotFoundException.class, () -> itemRequestController.getAll(-1L, 0, 10));
    }
}
