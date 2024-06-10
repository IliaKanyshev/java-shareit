package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.exception.*;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.util.Status.APPROVED;
import static ru.practicum.shareit.util.Status.WAITING;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingControllerTest {
    @Autowired
    private BookingController bookingController;

    @Autowired
    private UserController userController;

    @Autowired
    private ItemController itemController;

    private ItemDto itemDto;

    private UserDto userDto;

    private UserDto userDto1;

    private BookingDto bookingDto;

    @BeforeEach
    void init() {
        itemDto = ItemDto
                .builder()
                .name("name")
                .description("description")
                .available(true)
                .build();

        userDto = UserDto
                .builder()
                .name("name")
                .email("user@email.com")
                .build();

        userDto1 = UserDto
                .builder()
                .name("name")
                .email("user1@email.com")
                .build();

        bookingDto = BookingDto
                .builder()
                .start(LocalDateTime.of(2025, 10, 24, 12, 30))
                .end(LocalDateTime.of(2025, 11, 10, 13, 0))
                .itemId(1L).build();
    }

    @Test
    void createTest() {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        UserDto user1 = userController.add(userDto1);
        BookingDtoOut booking = bookingController.add(bookingDto, user1.getId());
        assertEquals(1L, bookingController.getBookingById(booking.getId(), user1.getId()).getId());
    }

    @Test
    void createByWrongUserTest() {
        assertThrows(UserNotFoundException.class, () -> bookingController.add(bookingDto, 1L));
    }

    @Test
    void createForWrongItemTest() {
        UserDto user = userController.add(userDto);
        assertThrows(ItemNotFoundException.class, () -> bookingController.add(bookingDto, 1L));
    }

    @Test
    void createByOwnerTest() {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        assertThrows(BookingOwnerException.class, () -> bookingController.add(bookingDto, 1L));
    }

    @Test
    void createToUnavailableItemTest() {
        UserDto user = userController.add(userDto);
        itemDto.setAvailable(false);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        UserDto user1 = userController.add(userDto1);
        assertThrows(BadRequestException.class, () -> bookingController.add(bookingDto, 2L));
    }

    @Test
    void approveTest() {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        UserDto user1 = userController.add(userDto1);
        BookingDtoOut booking = bookingController.add(BookingDto
                .builder()
                .start(LocalDateTime.of(2025, 10, 24, 12, 30))
                .end(LocalDateTime.of(2025, 11, 10, 13, 0))
                .itemId(item.getId()).build(), user1.getId());
        assertEquals(WAITING, bookingController.getBookingById(booking.getId(), user1.getId()).getStatus());
        bookingController.approve(booking.getId(), true, user.getId());
        assertEquals(APPROVED, bookingController.getBookingById(booking.getId(), user1.getId()).getStatus());
    }

    @Test
    void approveToWrongBookingTest() {
        assertThrows(UserNotFoundException.class, () -> bookingController.approve(1L, true, 1L));
    }

    @Test
    void approveByWrongUserTest() {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        UserDto user1 = userController.add(userDto1);
        BookingDtoOut booking = bookingController.add(bookingDto, user1.getId());
        assertThrows(BookingOwnerException.class, () -> bookingController.approve(1L, true, 2L));
    }

    @Test
    void approveToBookingWithWrongStatus() {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        UserDto user1 = userController.add(userDto1);
        BookingDtoOut booking = bookingController.add(bookingDto, user1.getId());
        bookingController.approve(1L, true, 1L);
        assertThrows(BadRequestException.class, () -> bookingController.approve(1L, true, 1L));
    }

    @Test
    void getAllByUserTest() {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        UserDto user1 = userController.add(userDto1);
        BookingDtoOut booking = bookingController.add(bookingDto, user1.getId());
        assertEquals(1, bookingController.getAllByUser("WAITING", user1.getId(), 0, 10).size());
        assertEquals(1, bookingController.getAllByUser("ALL", user1.getId(), 0, 10).size());
        assertEquals(0, bookingController.getAllByUser("PAST", user1.getId(), 0, 10).size());
        assertEquals(0, bookingController.getAllByUser("CURRENT", user1.getId(), 0, 10).size());
        assertEquals(1, bookingController.getAllByUser("FUTURE", user1.getId(), 0, 10).size());
        assertEquals(0, bookingController.getAllByUser("REJECTED", user1.getId(), 0, 10).size());
        bookingController.approve(booking.getId(), true, user.getId());
        assertEquals(0, bookingController.getAllByOwner("CURRENT", user.getId(), 0, 10).size());
        assertEquals(1, bookingController.getAllByOwner("ALL", user.getId(), 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner("WAITING", user.getId(), 0, 10).size());
        assertEquals(1, bookingController.getAllByOwner("FUTURE", user.getId(), 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner("REJECTED", user.getId(), 0, 10).size());
        assertEquals(0, bookingController.getAllByOwner("PAST", user.getId(), 0, 10).size());
    }

    @Test
    void getAllByWrongUserTest() {
        assertThrows(UserNotFoundException.class, () -> bookingController.getAllByUser("ALL", 1L, 0, 10));
        assertThrows(UserNotFoundException.class, () -> bookingController.getAllByOwner("ALL", 1L, 0, 10));
    }

    @Test
    void getByWrongIdTest() {
        assertThrows(BookingNotFoundException.class, () -> bookingController.getBookingById(1L, 1L));
    }

    @Test
    void getByWrongUser() {
        UserDto user = userController.add(userDto);
        ItemDtoOut item = itemController.add(user.getId(), itemDto);
        UserDto user1 = userController.add(userDto1);
        BookingDtoOut booking = bookingController.add(bookingDto, user1.getId());
        assertThrows(BookingOwnerException.class, () -> bookingController.getBookingById(1L, 10L));
    }
}
