package ru.practicum.shareit.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
public class ItemRequest {
    @NotNull
    private Long id;
    @Size(min = 1, max = 200, message = "description cant be more than 200 symbols")
    private String description;
    @NotBlank
    private User requester;
    @NotNull(message = "item creation dateTime is null")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH-mm-ss yyyy-MM-dd")
    private LocalDateTime created;
}
