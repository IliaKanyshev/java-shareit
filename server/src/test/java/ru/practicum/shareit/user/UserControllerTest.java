package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.exception.UserNotFoundException;

import java.util.Collections;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTest {
    private final ObjectMapper objectMapper;
    private final MockMvc mvc;
    @MockBean
    UserService userService;

    User user;

    UserDto userDto;
    UserDtoShort userDtoShort;

    UserDto updatedUser;

    @BeforeEach
    void init() {
        user = User.builder()
                .id(1L)
                .name("test")
                .email("test@mail.ru")
                .build();

        userDto = UserMapper.toUserDto(user);
        userDtoShort = UserMapper.toUserDtoShort(user);
        updatedUser = UserDto.builder()
                .id(1L)
                .name("test_updated")
                .email("updated@mail.ru")
                .build();
    }

    @Test
    @SneakyThrows
    void getAllTest() {
        when(userService.getAll()).thenReturn(Collections.singletonList(userDto));

        mvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("test"))
                .andExpect(jsonPath("$[0].email").value("test@mail.ru"));
    }

    @Test
    @SneakyThrows
    void getByIdTest() {
        when(userService.getById(1L)).thenReturn(userDto);
        mvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@mail.ru"));
    }

    @Test
    @SneakyThrows
    void getByIdUserNotFound() {
        when(userService.getById(2L)).thenThrow(new UserNotFoundException("User with id 2 not found"));
        mvc.perform(get("/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(UserNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("User with id 2 not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    @SneakyThrows
    void createTest() {
        when(userService.add(any())).thenReturn(userDto);
        mvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test"))
                .andExpect(jsonPath("$.email").value("test@mail.ru"));
    }

    @Test
    @SneakyThrows
    void updateTest() {
        when(userService.update(1L, updatedUser)).thenReturn(updatedUser);

        mvc.perform(patch("/users/1").content(objectMapper.writeValueAsString(updatedUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("test_updated"))
                .andExpect(jsonPath("$.email").value("updated@mail.ru"));
    }

    @Test
    @SneakyThrows
    void deleteTest() {
        mvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
