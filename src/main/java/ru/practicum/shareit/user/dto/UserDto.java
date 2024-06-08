package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.util.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "name cant be blank")
    private String name;
    @Email(groups = {Marker.OnCreate.class, Marker.OnUpdate.class}, message = "wrong email format")
    @NotBlank(groups = Marker.OnCreate.class)
    private String email;
}
