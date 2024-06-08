package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserRepositoryTest {
    private User user1;
    private User createdUser1;
    private final UserStorage userRepository;

    @BeforeEach
    public void beforeEach() {
        user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@mail.ru");
        createdUser1 = userRepository.save(user1);
    }

    @Test
    void getByIdAndCheck_isValid() {
        Optional<User> user = userRepository.findById(createdUser1.getId());
        assertTrue(user.isPresent());
        assertEquals(user1.getName(), user.get().getName());
    }

    @Test
    void getByIdAndCheck_isInvalid() {
        assertFalse(userRepository.findById(999L).isPresent());
    }
}
