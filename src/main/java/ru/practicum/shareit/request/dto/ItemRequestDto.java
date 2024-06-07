package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDto {
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(groups = Marker.OnCreate.class, max = 200, message = "description cant be more than 200 symbols")
    private String description;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH-mm-ss yyyy-MM-dd")
    private LocalDateTime created;
}
