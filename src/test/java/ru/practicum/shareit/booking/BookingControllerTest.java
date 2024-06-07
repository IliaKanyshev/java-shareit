package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.util.Status;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    BookingService bookingService;

    UserDtoShort user;
    ItemDtoShort item;
    BookingDtoOut booking;
    BookingDto bookingDto;

    @BeforeEach
    public void init() {
        user = UserDtoShort.builder()
                .id(1L)
                .name("user1")
                .build();

        item = ItemDtoShort.builder()
                .id(1L)
                .name("item1")
                .build();

        booking = BookingDtoOut.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .status(Status.WAITING)
                .item(item)
                .booker(user)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .start(LocalDateTime.now().plusHours(1))
                .end(LocalDateTime.now().plusHours(2))
                .itemId(1L)
                .build();
    }

    @Test
    @SneakyThrows
    public void bookingAddTest() {
        when(bookingService.add(any(), anyLong())).thenReturn(booking);
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("item1"))
                .andExpect(jsonPath("$.booker.name").value("user1"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void bookingAddInvalidTest() {
        when(bookingService.add(any(), anyLong())).thenReturn(booking);
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now().plusHours(1));
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @SneakyThrows
    public void getByIdTest() {
        when(bookingService.getById(any(), anyLong())).thenReturn(booking);
        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                // .andExpect(jsonPath("$.item.id").value(1L))
                .andExpect(jsonPath("$.item.name").value("item1"))
                .andExpect(jsonPath("$.booker.name").value("user1"))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    public void approveStatusTest() {
        booking.setStatus(Status.APPROVED);
        when(bookingService.approve(any(), anyLong(), anyBoolean())).thenReturn(booking);
        mvc.perform(patch("/bookings/1?approved=true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @SneakyThrows
    public void getAllByUserTest() {
        when(bookingService.getAllByUser(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(booking));
        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("item1"))
                .andExpect(jsonPath("$[0].booker.name").value("user1"));
    }

    @Test
    @SneakyThrows
    public void getAllByOwnerTest() {
        when(bookingService.getAllByUser(anyLong(), any(Status.class), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(booking));
        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].item.id").value(1L))
                .andExpect(jsonPath("$[0].item.name").value("item1"))
                .andExpect(jsonPath("$[0].booker.name").value("user1"));
    }

}
