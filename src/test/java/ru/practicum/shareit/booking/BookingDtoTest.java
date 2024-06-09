package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
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
    ItemDtoShort itemDtoShort = ItemMapper.toItemDtoShort(item);
    UserDtoShort userDtoShort = UserMapper.toUserDtoShort(user);
    private final BookingDtoOut booking = BookingDtoOut.builder()
            .id(1L)
            .start(LocalDateTime.now())
            .end(LocalDateTime.now())
            .status(Status.WAITING)
            .item(itemDtoShort)
            .booker(UserMapper.toUserDtoShort(user))
            .build();
    @Autowired
    private JacksonTester<BookingDtoOut> json;

    @Test
    @SneakyThrows
    void bookingDtoTest() {
        JsonContent<BookingDtoOut> result = json.write(booking);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo("item");
        assertThat(result).extractingJsonPathStringValue("$.booker.name").isEqualTo("user");
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}
