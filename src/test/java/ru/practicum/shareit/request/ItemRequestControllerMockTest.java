package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestControllerMockTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    ItemRequestService itemRequestService;
    User user;
    ItemRequestDtoOut itemRequestDtoOut;

    ItemRequestDto itemRequestDto;

    @BeforeEach
    void init() {
        user = new User(
                1L,
                "name",
                "email@email.ru");

        itemRequestDtoOut = new ItemRequestDtoOut(
                1L,
                1L,
                "description",
                LocalDateTime.now(),
                new ArrayList<>());

        itemRequestDto = new ItemRequestDto(
                "description",
                LocalDateTime.now());
    }

    @Test
    @SneakyThrows
    void createTest() {
        when(itemRequestService.add(anyLong(), any())).thenReturn(itemRequestDtoOut);
        mvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.requesterId").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    @SneakyThrows
    void getRequestsInfoTest() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        ItemRequestDtoOut itemRequestDto1 = ItemRequestMapper.toItemRequestDtoOut(itemRequest);
        when(itemRequestService.getAllByUserId(anyLong())).thenReturn(Collections.singletonList(itemRequestDto1));
        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void getRequestInfoTest() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        ItemRequestDtoOut request = ItemRequestMapper.toItemRequestDtoOut(itemRequest);
        when(itemRequestService.getById(anyLong(), anyLong())).thenReturn(request);
        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requesterId").value(1L))
                .andExpect(jsonPath("$.description").value("description"));
    }

    @Test
    @SneakyThrows
    void getRequestsListTest() {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto, user);
        ItemRequestDtoOut req = ItemRequestMapper.toItemRequestDtoOut(itemRequest);
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(Collections.singletonList(req));

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].requesterId").value(1L))
                .andExpect(jsonPath("$[0].description").value("description"));
    }

    @Test
    @SneakyThrows
    void getRequestsListTest_Bad() {
        mvc.perform(get("/requests/all?from=0&size=0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isBadRequest());
    }

}
