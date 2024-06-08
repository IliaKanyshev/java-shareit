package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserStorage userStorage;
    @InjectMocks
    private UserServiceImpl userService;

    User user;

    UserDto userDto;

    UserDto updatedUser;


    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();

        userDto = UserMapper.toUserDto(user);
        updatedUser = UserDto.builder()
                .id(1L)
                .name("test_updated")
                .email("updated@mail.ru")
                .build();
    }

    @Test
    void getAllEmptyListTest() {
        when(userStorage.findAll())
                .thenReturn(Collections.emptyList());
        assertTrue(userService.getAll().isEmpty());
    }

    @Test
    void getByIdTest() {
        when(userStorage.findById(0L))
                .thenReturn(Optional.of(user));
        var actualUser = userService.getById(0L);
        assertEquals(userDto, actualUser);
    }

    @Test
    void getByIdUserNotFoundTest() {
        when(userStorage.findById(0L))
                .thenReturn(Optional.empty());
        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userService.getById(0L));
        assertEquals("User with id=0 not found", e.getMessage());
    }

    @Test
    void userAddSuccessTest() {
        when(userStorage.save(any())).thenReturn(user);
        var actualUserDto = userService.add(userDto);
        assertEquals(userDto, actualUserDto);
        verify(userStorage).save(any());
    }

    @Test
    void updateUserNotFoundExceptionTest() {
        when(userStorage.findById(0L))
                .thenReturn(Optional.empty());
        UserNotFoundException e = assertThrows(UserNotFoundException.class,
                () -> userService.update(0L, updatedUser));
        assertEquals("User not found.", e.getMessage());
    }

    @Test
    void updateEmailTest() {
        User userBefore = User.builder()
                .id(1L)
                .email("email@mail.ru")
                .name("name")
                .build();

        UserDto userAfter = UserDto.builder()
                .email("updatemail@mail.ru").build();
        when(userStorage.save(any())).thenReturn(userBefore);
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(userBefore));

        UserDto updated = userService.update(1L, userAfter);

        assertEquals(updated.getEmail(), userAfter.getEmail());
    }

    @Test
    void updateNewNameTest() {
        UserDto userAfter = UserDto.builder()
                .name("newmail@mail.ru")
                .build();
        when(userStorage.save(any())).thenReturn(user);
        when(userStorage.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));

        UserDto actual = userService.update(1L, userAfter);

        assertEquals(actual.getName(), userAfter.getName());
    }

    @Test
    void deleteTest() {
        when(userStorage.findById(1L)).thenReturn(Optional.of(user));
        userService.delete(1L);
        verify(userStorage).deleteById(any());
    }
}
