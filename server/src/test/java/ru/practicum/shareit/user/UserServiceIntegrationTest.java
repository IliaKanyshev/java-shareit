package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserStorage userStorage;
    private final UserService userService;

    @Test
    public void addUserTest() {
        User user = new User(1L, "user", "user@mail.com");
        UserDto userDto = UserMapper.toUserDto(user);
        UserDto addedUser = userService.add(userDto);

        assertNotNull(addedUser);
        assertEquals(user.getName(), addedUser.getName());
        assertEquals(user.getEmail(), addedUser.getEmail());
    }

    @Test
    @SneakyThrows
    public void getUserByIdTest() {
        User user = userStorage.save(new User(1L, "user", "user@mail.com"));
        UserDto userDto = UserMapper.toUserDto(user);
        UserDto retrievedUser = userService.getById(user.getId());
        assertNotNull(retrievedUser);
        assertEquals(user.getId(), retrievedUser.getId());
        assertEquals(user.getName(), retrievedUser.getName());
        assertEquals(user.getEmail(), retrievedUser.getEmail());
    }

    @Test
    public void getUserByIdThrowsUserNotFoundExceptionTest() {
        assertThrows(UserNotFoundException.class, () -> userService.getById(10L));
    }

    @Test
    public void getAllUsersTest() {
        User user1 = new User(2L, "user1", "user1@mail.ru");
        User user2 = new User(3L, "user2", "user2@mail.ru");
        List<User> users = userStorage.saveAll(List.of(user1, user2));
        assertEquals(2, users.size());
    }

    @Test
    @SneakyThrows
    public void updateUserTest() {
        User user = userStorage.save(new User(1L, "user", "user@mail.com"));
        User updatedUser = new User(2L, "Updated Name", null);
        UserDto updatedUserDto = UserMapper.toUserDto(updatedUser);
        UserDto result = userService.update(user.getId(), updatedUserDto);
        assertEquals(user.getId(), result.getId());
        assertEquals(updatedUser.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    public void deleteUserTest() {
        User user = userStorage.save(new User(1L, "user", "user@mail.com"));
        userService.delete(user.getId());
        assertFalse(userStorage.existsById(user.getId()));
    }
}
