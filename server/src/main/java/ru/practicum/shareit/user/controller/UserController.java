package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Marker;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        log.info("New request to create user.");
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        log.info("New request to update user.");
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        log.info("New request to get user with id {}", userId);
        return userService.getById(userId);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("New request to get list of all users.");
        return userService.getAll();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("New request to delete user with id {}", userId);
        userService.delete(userId);
    }
}
