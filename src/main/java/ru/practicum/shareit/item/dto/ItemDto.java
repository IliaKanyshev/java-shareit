package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "name cant be blank")
    private String name;
    @NotBlank(groups = Marker.OnCreate.class, message = "description cant be blank")
    @Size(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, min = 1, max = 200, message = "description cant be more than 200 symbols")
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    private User owner;
    private Long requestId;
}
