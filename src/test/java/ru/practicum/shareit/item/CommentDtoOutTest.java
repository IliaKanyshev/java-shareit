package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class CommentDtoOutTest {
    @Autowired
    private JacksonTester<CommentDtoOut> json;

    private final User user = new User(
            1L,
            "user",
            "user@user.ru");
    private final Item item = new Item(
            1L,
            "item",
            "description",
            true,
            user,
            null);

    private final Comment comment = new Comment(1L, "text", item, user, LocalDateTime.now());
    CommentDtoOut commentDtoOut = CommentMapper.toCommentDtoOut(comment);

    @Test
    void commentDto() throws Exception {
        var res = json.write(commentDtoOut);

        assertThat(res).hasJsonPath("$.id");
        assertThat(res).hasJsonPath("$.text");
        assertThat(res).hasJsonPath("$.authorName");
        assertThat(res).hasJsonPath("$.created");

        assertThat(res).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(res).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(res).extractingJsonPathStringValue("$.authorName").isEqualTo("user");
    }
}
