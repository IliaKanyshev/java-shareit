package ru.practicum.shareit.request.dto;


import lombok.*;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ItemRequestDtoOut {
    private Long id;
    private Long requesterId;
    private String description;
    private LocalDateTime created;
    private List<ItemDtoOut> items;
}
