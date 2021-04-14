package ru.razor.miro.managers;

import ru.razor.miro.dto.WidgetDTO;

import java.util.List;
import java.util.UUID;

public interface WidgetManager {
    WidgetDTO addWidget(int x, int y, int width, int height, Integer index);

    WidgetDTO changeWidget(UUID id, int x, int y, int width, int height, Integer index);

    void deleteWidget(UUID id);

    WidgetDTO getWidgetById(UUID id);

    List<WidgetDTO> getWidgetList();

}
