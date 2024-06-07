package ru.practicum.shareit.item;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


@JsonTest
public class ItemDtoOutTest {
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
    @Autowired
    private JacksonTester<ItemDtoOut> json;

    @Test
    @SneakyThrows
    void testItemDto() {
        ItemDtoOut itemDtoOut = ItemMapper.toItemDtoOut(item);
        JsonContent<ItemDtoOut> result = json.write(itemDtoOut);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathStringValue("$.lastBooking").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.nextBooking").isEqualTo(null);
    }
}
