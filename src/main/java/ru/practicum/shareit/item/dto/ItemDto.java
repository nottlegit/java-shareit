package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder(toBuilder = true)
public class ItemDto {
    private Long id;

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotNull(message = "Статус должен быть указан")
    private Boolean available;

    public Boolean hasName() {
        return !(name == null || name.isBlank());
    }

    public Boolean hasDescription() {
        return !(description == null || description.isBlank());
    }

    public Boolean hasAvailable() {
        return available != null;
    }
}
