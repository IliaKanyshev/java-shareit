package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    User add(User user);

    User update(Long userId, User user);

    void delete(Long userId);

    Optional<User> get(Long userId);

    Collection<User> getAll();
}
