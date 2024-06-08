package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.dao.BookingStorage;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.util.Status.APPROVED;
import static ru.practicum.shareit.util.Status.FUTURE;

@DataJpaTest
public class BookingStorageTest {

    @Autowired
    private UserStorage userStorage;
    @Autowired
    private ItemStorage itemStorage;
    @Autowired
    private BookingStorage bookingStorage;
    private User user;
    private User user2;
    private Item item;
    private Booking booking;
    private Booking booking2;
    private Sort sort;

    @BeforeEach
    void init() {
        user = User.builder()
                .name("user1")
                .email("user1@email.com")
                .build();

        user2 = User.builder()
                .name("user2")
                .email("user2@email.com")
                .build();

        item = Item.builder()
                .name("item")
                .description("desc")
                .available(true)
                .owner(user)
                .build();

        booking = Booking.builder()
                .start(LocalDateTime.of(2024, 1, 1, 1, 1))
                .end(LocalDateTime.of(2024, 2, 2, 2, 2))
                .item(item)
                .booker(user)
                .status(APPROVED)
                .build();

        booking2 = Booking.builder()
                .start(LocalDateTime.of(2025, 1, 1, 1, 1))
                .end(LocalDateTime.of(2025, 2, 2, 2, 2))
                .item(item)
                .booker(user2)
                .status(FUTURE)
                .build();

        sort = Sort.by(Sort.Direction.DESC, "start");

        userStorage.save(user);
        userStorage.save(user2);
        itemStorage.save(item);
        bookingStorage.save(booking);
        bookingStorage.save(booking2);
    }

    @AfterEach
    void tearDown() {
        bookingStorage.deleteAll();
        itemStorage.deleteAll();
        userStorage.deleteAll();
    }

    @Test
    void findAllByItemIdOrderByStartAscTest() {
        assertThat(bookingStorage.findFirstByItemIdAndStartLessThanEqualAndStatus(item.getId(),
                                LocalDateTime.of(2024, 1, 2, 1, 1),
                                APPROVED, sort)
                        .get()
                        .getStart(),
                equalTo(LocalDateTime.of(2024, 1, 1, 1, 1)));
    }

    @Test
    void findAllByBookerTest() {
        assertThat((long) bookingStorage.findAllByBookerIdOrderByStartDesc(user2.getId(),
                PageRequest.of(0, 10, sort)
        ).size(), equalTo(1L));
    }

    @Test
    void findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBeforeTest() {
        assertThat(bookingStorage.findAllByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(user.getId(),
                        item.getId(), APPROVED,
                        LocalDateTime.of(2024, 5, 5, 5, 15)).size(),
                equalTo(1));
    }

    @Test
    void findByItemInAndStartAfterAndStatusTest() {
        assertThat(bookingStorage.findByItemInAndStartAfterAndStatus(List.of(item),
                        LocalDateTime.of(2023, 1, 1, 1, 1),
                        APPROVED, sort).size(),
                equalTo(1));
    }

    @Test
    void findFirstByItemIdAndStartAfterAndStatusTest() {

        assertThat(bookingStorage.findFirstByItemIdAndStartAfterAndStatus(item.getId(),
                                LocalDateTime.of(2023, 1, 1, 1, 1),
                                APPROVED, sort).get()
                        .getStart(),
                equalTo(LocalDateTime.of(2024, 1, 1, 1, 1)));
    }

    @Test
    void findAllByOwnerIdAndStateFutureTest() {
        assertThat(bookingStorage.findAllByOwnerIdAndStateFuture(user.getId(),
                sort,
                PageRequest.of(0, 10)).size(), equalTo(1));
    }

    @Test
    void findAllByOwnerIdTest() {
        assertThat(bookingStorage.findAllByOwnerId(user.getId(), sort,
                        PageRequest.of(0, 10))
                .size(), equalTo(2));
    }

}
