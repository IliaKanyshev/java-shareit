package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;
import ru.practicum.shareit.item.dao.CommentStorage;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@RunWith(SpringRunner.class)
class CommentStorageTest {
    User user = new User(null, "user", "user@mail.ru");
    Item item = new Item(
            null,
            "name1",
            "desc1",
            true,
            user,
            null);
    Comment comment = new Comment(
            null,
            "text",
            item,
            user,
            LocalDateTime.now());
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentStorage commentStorage;

    @Test
    void contextLoads() {
        assertNotNull(em);
    }

    @Test
    void findAllComments() {
        em.persist(user);
        em.persist(item);
        em.persist(comment);
        List<Comment> comments = (List<Comment>) commentStorage.findAllByItemId(item.getId());
        assertEquals(1, comments.size());
        assertEquals(comment, comments.get(0));
    }
}
