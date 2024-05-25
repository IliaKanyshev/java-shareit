package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public UserDto add(UserDto userDto) {
        checkEmail(userDto);
        return UserMapper.toUserDto(userStorage.add(UserMapper.toUserFromDto(userDto)));
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        get(userId);
        userDto.setId(userId);
        checkEmail(userDto);
        User user = UserMapper.toUserFromDto(userDto);
        return UserMapper.toUserDto(userStorage.update(userId, user));
    }

    @Override
    public void delete(Long userId) {
        get(userId);
        userStorage.delete(userId);
    }

    @Override
    public UserDto get(Long userId) {
        return UserMapper.toUserDto(userStorage.get(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%d not found", userId))));
    }

    @Override
    public Collection<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void checkEmail(UserDto userDto) {
        if (userStorage.getAll().stream()
                .filter(u -> !Objects.equals(u.getId(), userDto.getId()))
                .map(User::getEmail)
                .anyMatch(e -> e.equals(userDto.getEmail()))) {
            throw new EmailAlreadyExistException(String.format("Email %s already in use.", userDto.getEmail()));
        }
    }
}
