package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.util.Status.APPROVED;
import static ru.practicum.shareit.util.Status.WAITING;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final EntityManager em;
    private final ItemService itemService;
    private final BookingService bookingService;

    private final User owner = User.builder().name("owner").email("owner@email.ru").build();
    private final User booker = User.builder().name("booker").email("booker@email.ru").build();
    private final Item item = Item.builder().name("name").available(true).description("item").owner(owner).build();
    private final Comment emptyComment = Comment.builder().build();
    private final Item otherItem = Item.builder().available(true).description("otherItem").owner(owner).build();
    private final Booking booking = Booking.builder()
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1))
            .item(item)
            .booker(booker)
            .status(WAITING)
            .build();
    private final Booking secondBooking = Booking.builder()
            .start(LocalDateTime.now().plusMinutes(5))
            .end(LocalDateTime.now().plusDays(1))
            .item(item)
            .booker(booker)
            .status(APPROVED)
            .build();


    @BeforeEach
    void setUp() {
        em.persist(owner);
        em.persist(booker);
        em.persist(item);
        em.persist(booking);
        em.persist(secondBooking);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void getItemsByOwnerTest() {
        List<ItemDtoOut> itemWithBookings = (List<ItemDtoOut>) itemService.getUserItems(owner.getId(), 0, 10);
        assertThat(itemWithBookings).isNotEmpty();
        assertEquals(1, itemWithBookings.size());
        assertThat(secondBooking.getStart()).isEqualTo(itemWithBookings.get(0).getNextBooking().getStart());
    }

    @Test
    void searchItemsTest() {
        String emptyText = "";
        List<ItemDtoOut> items = (List<ItemDtoOut>) itemService.search(emptyText, 0, 10);
        assertThat(items).isEmpty();
    }

    @Test
    void updateItemTest() {
        ItemDto itemDto = ItemDto.builder().name("newName").description("newDescr").build();
        itemService.update(item.getId(), owner.getId(), itemDto);
        TypedQuery<Item> query = em.createQuery("select i from Item i where i.name = :name", Item.class);
        Item itemRes = query.setParameter("name", "newName").getSingleResult();

        assertThat(itemRes.getDescription()).isEqualTo("newDescr");
        assertThat(itemRes.getItemRequest()).isNull();

        int hashCode = itemRes.hashCode();
        assertThat(itemRes.getClass().hashCode()).isEqualTo(hashCode);
    }
}
