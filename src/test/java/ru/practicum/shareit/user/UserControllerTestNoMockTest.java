package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTestNoMockTest {
    @Autowired
    private UserController userController;

    private UserDto user;

    @BeforeEach
    void init() {
        user = UserDto.builder()
                .name("name")
                .email("user@email.com")
                .build();
    }

    @Test
    void createTest() {
        UserDto userDto = userController.add(user);
        assertEquals(userDto.getId(), userController.get(userDto.getId()).getId());
    }

    @Test
    void updateTest() {
        userController.add(user);
        UserDto userDto = UserDto.builder().name("update name").email("update@email.com").build();
        userController.update(1L, userDto);
        assertEquals(userDto.getEmail(), userController.get(1L).getEmail());
    }

    @Test
    void updateByWrongUserTest() {
        assertThrows(UserNotFoundException.class, () -> userController.update(1L, user));
    }

    @Test
    void deleteTest() {
        UserDto userDto = userController.add(user);
        assertEquals(1, userController.getAll().size());
        userController.delete(userDto.getId());
        assertEquals(0, userController.getAll().size());
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(UserNotFoundException.class, () -> userController.get(1L));
    }
}
