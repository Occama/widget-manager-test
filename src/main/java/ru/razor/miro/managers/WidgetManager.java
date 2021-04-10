package ru.razor.miro.managers;

import ru.razor.miro.entities.Widget;

import java.util.List;
import java.util.UUID;

public interface WidgetManager {
    public Widget addWidget(int x, int y, int width, int height, int index);

    public Widget changeWidget(UUID id, int x, int y, int width, int height, int index);

    public void deleteWidget(UUID id);

    public Widget getWidgetById(UUID id);

    public List<Widget> getWidgetList();

}
