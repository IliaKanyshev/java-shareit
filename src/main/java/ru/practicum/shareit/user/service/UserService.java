package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {
    UserDto add(UserDto userDto);

    UserDto update(Long userId, UserDto userDto);

    void delete(Long userId);

    UserDto get(Long userId);

    Collection<UserDto> getAll();

    void checkEmail(UserDto userDto);
}
