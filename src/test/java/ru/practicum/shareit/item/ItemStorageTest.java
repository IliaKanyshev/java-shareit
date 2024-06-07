package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase
@RunWith(SpringRunner.class)
class ItemStorageTest {
    User user = new User(null, "user", "user@mail.ru");
    Item item = new Item(
            null,
            "item1",
            "desc1",
            true,
            user,
            null);
    Item item2 = new Item(
            null,
            "item2",
            "desc2",
            true,
            user,
            null);
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemStorage itemStorage;

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllByOwnerId() {
        em.persist(user);
        em.persist(item);
        em.persist(item2);
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<Item> items = (List<Item>) itemStorage.findAllByOwnerId(user.getId(), pageRequest);
        assertEquals(item, items.get(0));
        assertEquals(item2, items.get(1));
    }

    @Test
    void searchTest() {
        em.persist(user);
        em.persist(item);
        em.persist(item2);
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<Item> items = itemStorage.search("esc1", pageRequest);
        assertEquals(1, items.size());
        assertEquals("item1", items.get(0).getName());
    }

    @Test
    void findByItemRequestIdTest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Description")
                .requester(user)
                .created(LocalDateTime.now())
                .build();


        Item itemWithRequest = new Item(
                null,
                "name3",
                "aaaaaa",
                true,
                user,
                itemRequest);

        em.persist(user);
        em.persist(item);
        em.persist(item2);
        em.persist(itemWithRequest);
        List<Item> items = itemStorage.findByItemRequestId(1L);
        assertEquals("name3", items.get(0).getName());
        assertEquals("aaaaaa", items.get(0).getDescription());
    }

    @Test
    void searchByRequestIdsTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Description");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        Item itemWithRequest = new Item(
                null,
                "name3",
                "bbbbbb",
                true,
                user,
                itemRequest);
        em.persist(user);
        em.persist(item);
        em.persist(item2);
        em.persist(itemWithRequest);
        List<Item> items = itemStorage.searchByRequestIds(List.of(itemWithRequest.getItemRequest().getId()));
        assertEquals(1, items.size());
        assertEquals("name3", items.get(0).getName());
        assertEquals("bbbbbb", items.get(0).getDescription());
    }
    }

