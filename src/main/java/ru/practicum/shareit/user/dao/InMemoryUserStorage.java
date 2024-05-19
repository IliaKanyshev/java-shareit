package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.exception.OwnerException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private Long counter = 0L;

    @Override
    public User add(User user) {
        user.setId(++counter);
        users.put(user.getId(), user);
        log.info("User {} created.", user.getName());
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        User user1 = users.get(userId);
        if (!user.getId().equals(user1.getId())) {
            throw new OwnerException("Only owner can update user info.");
        }
        if (user.getEmail() != null) user1.setEmail(user.getEmail());
        if (user.getName() != null) user1.setName(user.getName());
        users.put(user.getId(), user1);
        log.info("User {} was updated.", user.getName());
        return user1;
    }

    @Override
    public void delete(Long userId) {
        log.info("User with {} was deleted.", userId);
        users.remove(userId);
    }

    @Override
    public Optional<User> get(Long userId) {
        log.info("Getting user with id {}", userId);
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public Collection<User> getAll() {
        log.info("List of all users: ");
        return new ArrayList<>(users.values());
    }
}
