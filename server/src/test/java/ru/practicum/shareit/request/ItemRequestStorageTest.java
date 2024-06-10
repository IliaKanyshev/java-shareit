package ru.practicum.shareit.request;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class ItemRequestStorageTest {
    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private ItemRequestStorage itemRequestStorage;
    private User user;
    private User user2;
    private ItemRequest itemRequest;
    private ItemRequest itemRequest2;
    @Autowired
    private TestEntityManager em;


    @BeforeEach
    void init() {
        user = User.builder()
                .name("user1")
                .email("user1@mail.ru")
                .build();

        user2 = User.builder()
                .name("user2")
                .email("user2@mail.ru")
                .build();

        itemRequest = ItemRequest.builder()
                .id(1L)
                .description("descr")
                .created(LocalDateTime.now().plusHours(2))
                .requester(user)
                .build();

        itemRequest2 = ItemRequest.builder()
                .id(2L)
                .description("descr")
                .created(LocalDateTime.now().plusHours(2))
                .requester(user2)
                .build();

        userStorage.save(user);
        itemRequestStorage.save(itemRequest);
        userStorage.save(user2);

        itemRequestStorage.save(itemRequest2);
    }

    @AfterEach
    void tearDown() {
        itemRequestStorage.deleteAll();
        itemStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByRequesterIdOrderByCreatedAscTest() {
        assertThat((itemRequestStorage.findAllByRequesterId(user.getId(), Sort.by(Sort.Direction.ASC, "created"))
                .size()), equalTo(1));
    }

    @Test
    void findAllByRequesterIdNotLikeOrderByCreatedAscTest() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "created"));
        assertThat(itemRequestStorage.findAllByRequesterIdIsNot(user.getId(), pageRequest).size(), equalTo(1));
    }
}
