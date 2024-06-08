package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.util.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemControllerMockTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    ItemService itemService;

    User user;
    UserDto userDto;
    Item item;
    ItemDto itemDto;
    ItemDtoOut itemDtoUpdated;
    ItemDtoOut itemDtoOut;

    @BeforeEach
    public void init() {

        itemDto = ItemDto.builder()
                .id(1L)
                .name("name")
                .description("desc")
                .available(true)
                .requestId(null)
                .build();

        item = Item.builder()
                .id(1L)
                .itemRequest(null)
                .owner(user)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        itemDtoUpdated = ItemDtoOut.builder()
                .id(1L)
                .name("updated")
                .description("updated desc")
                .available(true)
                .requestId(null)
                .build();

        user = User.builder()
                .id(1L)
                .name("name")
                .email("user@mail.ru")
                .build();

        item = Item.builder()
                .id(1L)
                .itemRequest(null)
                .owner(user)
                .name("name")
                .description("desc")
                .available(true)
                .build();

        itemDtoOut = ItemMapper.toItemDtoOut(item);
        userDto = UserMapper.toUserDto(user);
    }

    @Test
    @SneakyThrows
    void addItemTest() {
        when(itemService.add(1L, itemDto)).thenReturn(itemDtoOut);
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    @SneakyThrows
    void getAllTest() {
        User user = UserMapper.toUserFromDto(userDto);
        Item item = ItemMapper.toItemFromDto(itemDto, null);
        when(itemService.getUserItems(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(itemDtoOut));
        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("desc"));
    }

    @Test
    @SneakyThrows
    void findItemTest() {
        when(itemService.getById(anyLong(), anyLong())).thenReturn(itemDtoOut);
        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("desc"));
    }


    @Test
    @SneakyThrows
    void updateTest() {
        when(itemService.update(anyLong(), any(), any())).thenReturn(itemDtoUpdated);
        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDtoUpdated))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.description").value("updated desc"));
    }

    @Test
    @SneakyThrows
    void updateItemNotFoundTest() {
        ItemDto itemDto1 = new ItemDto(1L, "updated", "updated description", true, null);
        when(itemService.update(anyLong(), any(), any())).thenThrow(new ItemNotFoundException("Item not found."));
        mvc.perform(patch("/items/1")
                        .content(objectMapper.writeValueAsString(itemDto1))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ItemNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("Item not found.",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void searchItemTest() {
        when(itemService.search(anyString(), anyInt(), anyInt())).thenReturn(Collections.singletonList(itemDtoOut));
        mvc.perform(get("/items/search?text=дрель")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("desc"));
    }

    @Test
    @SneakyThrows
    void addCommentTest() {
        CommentDtoOut commentDtoOut = CommentDtoOut.builder()
                .id(1L)
                .text("Test")
                .authorName(user.getName()).build();
        when(itemService.addComment(anyLong(), anyLong(), any())).thenReturn(commentDtoOut);
        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentDtoOut)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("Test"))
                .andExpect(jsonPath("$.authorName").value(commentDtoOut.getAuthorName()));
    }
}
