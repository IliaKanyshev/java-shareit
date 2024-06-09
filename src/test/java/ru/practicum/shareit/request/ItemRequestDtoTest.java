package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
public class ItemRequestDtoTest {
    @Autowired
    JacksonTester<ItemRequestDtoOut> json;

    @Test
    void testItemRequestDto() throws Exception {
        ItemRequestDtoOut itemRequestDtoOut = ItemRequestDtoOut
                .builder()
                .id(1L)
                .description("descriptionOfItemRequest")
                .created(LocalDateTime.of(2025, 10, 24, 12, 30))
                .build();

        JsonContent<ItemRequestDtoOut> result = json.write(itemRequestDtoOut);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("descriptionOfItemRequest");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo("2025-10-24T12:30:00");
    }
}
