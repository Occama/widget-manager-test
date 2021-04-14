package ru.razor.miro.dto;

import org.springframework.core.convert.converter.Converter;
import ru.razor.miro.entities.Widget;

public class WidgetToDTOConverter implements Converter<Widget, WidgetDTO> {

    @Override
    public WidgetDTO convert(Widget widget) {
        return WidgetDTO.builder()
                .id(widget.getId())
                .x(widget.getX())
                .y(widget.getY())
                .index(widget.getIndex())
                .width(widget.getWidth())
                .height(widget.getHeight())
                .modified(widget.getModified())
                .build();
    }
}
