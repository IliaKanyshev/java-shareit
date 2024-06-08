package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestIntegrationTest {
    private final EntityManager em;
    private final ItemRequestService itemRequestService;

    private final User owner = User.builder().name("owner").email("owner@mail.ru").build();
    private final Item item = Item.builder().name("item").available(true).description("item").owner(owner).build();
    private final ItemRequest emptyRequest = ItemRequest.builder().build();
    private final User requester = User.builder().name("requester").email("requestor@mail.ru").build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("needItem").build();

    @BeforeEach
    void setUp() {
        em.persist(requester);
        em.persist(owner);
        em.persist(item);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void createItemRequest() {
        itemRequestService.add(requester.getId(), itemRequestDto);
        TypedQuery<ItemRequest> query = em.createQuery("select i from ItemRequest i where i.description = :desc", ItemRequest.class);
        ItemRequest itemRequest = query.setParameter("desc", "needItem").getSingleResult();

        assertThat(itemRequest).isNotNull();
        assertThat(itemRequest.getRequester()).isEqualTo(requester);
        assertThat(itemRequest.getCreated()).isNotNull();
        assertThat(itemRequest.getDescription()).isEqualTo("needItem");
        assertThat(itemRequest).isNotEqualTo(emptyRequest);
    }

//    @Test
//    void getAllRequestsByUser() {
//        ItemRequest itemRequest = ItemRequest.builder()
//                .created(LocalDateTime.now()).requester(requester).description("smth").build();
//        em.persist(itemRequest);
//
//        List<ItemRequestDtoOut> itemRequestList = itemRequestService.getAll(owner.getId(), 0, 10);
//        assertThat(itemRequestList.size()).isEqualTo(1);
//
//        List<ItemRequestDtoOut> emptyRequestList = itemRequestService.getAll(requester.getId(), 0, 10);
//        assertThat(emptyRequestList.size()).isEqualTo(0);
//    }
}
