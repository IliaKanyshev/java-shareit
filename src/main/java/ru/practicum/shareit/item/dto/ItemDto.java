package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "name cant be blank")
    private String name;
    @NotBlank(groups = Marker.OnCreate.class, message = "description cant be blank")
    @Size(groups = {Marker.OnCreate.class, Marker.OnUpdate.class},
            min = 1, max = 200, message = "description cant be more than 200 symbols")
    private String description;
    @NotNull(groups = Marker.OnCreate.class)
    private Boolean available;
    private Long requestId;
}
