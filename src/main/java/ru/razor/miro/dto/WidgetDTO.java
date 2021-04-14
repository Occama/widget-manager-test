package ru.razor.miro.dto;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Jacksonized
@Builder
public class WidgetDTO {
    private UUID id;

    private int x;
    private int y;

    private int index;

    private int width;
    private int height;

    private LocalDateTime modified;
}
