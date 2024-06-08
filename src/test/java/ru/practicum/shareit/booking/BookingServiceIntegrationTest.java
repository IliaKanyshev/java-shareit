package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Status;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static ru.practicum.shareit.util.Status.REJECTED;
import static ru.practicum.shareit.util.Status.WAITING;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final EntityManager em;
    private final BookingService bookingService;

    private final User owner = User.builder().name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().name("booker").email("booker@email.ru").build();
    private final Item item = Item.builder().name("item").available(true).description("item").owner(owner).build();

    private final Booking booking = Booking.builder()
            .start(LocalDateTime.now().plusDays(1))
            .end(LocalDateTime.now().plusDays(3))
            .item(item)
            .booker(booker)
            .status(WAITING)
            .build();

    private final int from = 0;
    private final int size = 10;

    @BeforeEach
    void setUp() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void createBooking() {
        User otherBooker = User.builder().name("otherBooker").email("otherBooker@mail.ru").build();
        em.persist(otherBooker);
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        bookingService.add(bookingDto, otherBooker.getId());

        TypedQuery<Booking> query = em.createQuery("select b from Booking b where b.booker = :booker", Booking.class);
        Booking bookingRes = query.setParameter("booker", otherBooker).getSingleResult();

        assertThat(bookingRes).isNotNull();
        assertThat(bookingRes.getStatus()).isEqualTo(WAITING);

        int hashCode = booking.hashCode();
        Assertions.assertThat(booking.getClass().hashCode()).isEqualTo(hashCode);
    }

    @Test
    void getAllByBookerWithFutureStateTest() {
        List<BookingDtoOut> bookings = (List<BookingDtoOut>) bookingService.getAllByUser(booker.getId(), Status.FUTURE, from, size);
        assertThat(bookings.size()).isEqualTo(1);
        assertThat(bookings.get(0).getStart()).isEqualTo(booking.getStart());
        assertThat(bookings.get(0).getEnd()).isEqualTo(booking.getEnd());
    }

    @Test
    void approveTest() {
        assertThat(booking).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(WAITING);

        bookingService.approve(owner.getId(), booking.getId(), false);
        assertThat(booking.getStatus()).isEqualTo(REJECTED);

    }
}
