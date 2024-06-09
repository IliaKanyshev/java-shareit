package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.exception.EmailAlreadyExistException;
import ru.practicum.shareit.util.exception.UserNotFoundException;

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
        // log.info("Creating new user {}", userDto.getName());
        User user = userStorage.save(UserMapper.toUserFromDto(userDto));

        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User user = userStorage.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        log.info("User with id {} has been updated", userId);
        return UserMapper.toUserDto(userStorage.save(user));
    }

    @Override
    public void delete(Long userId) {
        getById(userId);
        userStorage.deleteById(userId);
        log.info("User with id {} has been deleted.", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long userId) {
        return UserMapper.toUserDto(userStorage.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.format("User with id=%d not found", userId))));
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<UserDto> getAll() {
        log.info("Users list: ");
        return userStorage.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void checkEmail(UserDto userDto) {
        if (userStorage.findAll().stream()
                .filter(u -> !Objects.equals(u.getId(), userDto.getId()))
                .map(User::getEmail)
                .anyMatch(e -> e.equals(userDto.getEmail()))) {
            throw new EmailAlreadyExistException(String.format("Email %s already in use.", userDto.getEmail()));
        }
    }
}
