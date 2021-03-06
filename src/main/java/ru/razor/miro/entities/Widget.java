package ru.razor.miro.entities;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class Widget {
    @Setter(AccessLevel.NONE)
    private UUID id;

    private int x;
    private int y;

    private int index;

    @Min(value = 1, message = "Widget width must be positive")
    private int width;
    @Min(value = 1, message = "Widget height must be positive")
    private int height;

    private LocalDateTime modified;
}
