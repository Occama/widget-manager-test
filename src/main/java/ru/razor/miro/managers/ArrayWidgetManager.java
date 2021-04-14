package ru.razor.miro.managers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.razor.miro.dto.WidgetDTO;
import ru.razor.miro.dto.WidgetToDTOConverter;
import ru.razor.miro.entities.Widget;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Component
public class ArrayWidgetManager implements WidgetManager {
    private final List<Widget> widgetContainer;
    private final ReentrantReadWriteLock rwLock;
    private final WidgetToDTOConverter converter;

    @Autowired
    public ArrayWidgetManager(List<Widget> widgetContainer) {
        this.widgetContainer = widgetContainer;
        rwLock = new ReentrantReadWriteLock(true);
        converter = new WidgetToDTOConverter();
    }

    @Override
    public WidgetDTO addWidget(int x, int y, int width, int height, Integer index) {
        rwLock.writeLock().lock();
        try {
            return addWidget(UUID.randomUUID(), x, y, width, height, index);
        }
        finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public WidgetDTO changeWidget(UUID id, int x, int y, int width, int height, Integer index) {
        Widget widget;
        WidgetDTO widgetDTO;

        rwLock.writeLock().lock();
        try {
            int position = getWidgetPositionByID(id);

            widget = widgetContainer.get(position);

            if (widget.getIndex() == index) {
                widget.setX(x);
                widget.setY(y);
                widget.setWidth(width);
                widget.setHeight(height);
                widget.setModified(LocalDateTime.now());
                widgetDTO = converter.convert(widget);
            }
            else {
                widgetContainer.remove(position);
                widgetDTO = addWidget(widget.getId(), x, y, width, height,index);
            }
            return widgetDTO;
        }
        finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public void deleteWidget(UUID id) {
        rwLock.writeLock().lock();
        try {
            widgetContainer.remove(getWidgetPositionByID(id));
        }
        finally {
            rwLock.writeLock().unlock();
        }
    }

    @Override
    public WidgetDTO getWidgetById(UUID id) {
        rwLock.readLock().lock();
        try {
            return converter.convert( widgetContainer.get(getWidgetPositionByID(id)) );
        }
        finally {
            rwLock.readLock().unlock();
        }
    }

    @Override
    public List<WidgetDTO> getWidgetList() {
        rwLock.readLock().lock();
        try {
            return widgetContainer.stream().map(converter::convert).collect(Collectors.toUnmodifiableList());
        }
        finally {
            rwLock.readLock().unlock();
        }
    }

    public void clearList() {
        rwLock.writeLock().lock();
        try {
            widgetContainer.clear();
        }
        finally {
            rwLock.writeLock().unlock();
        }
    }

    private WidgetDTO addWidget(UUID id, int x, int y, int width, int height, Integer index) {
        Widget widget;
        if (index != null) {
            int position = shiftIndices(index);
            widget = new Widget(id, x, y, index, width, height, LocalDateTime.now());
            widgetContainer.add(position, widget);
        }
        else {
            widget = new Widget(UUID.randomUUID()
                    , x
                    , y
                    , widgetContainer.get(widgetContainer.size()-1).getIndex()+1
                    , width
                    , height
                    , LocalDateTime.now());
            widgetContainer.add(widget);
        }
        return converter.convert(widget);
    }

    private int shiftIndices(final int firstIndex) {
        //Searching for a position of the new widget
        int shiftPosition = 0;
        while (shiftPosition < widgetContainer.size() && widgetContainer.get(shiftPosition).getIndex() < firstIndex) {
            ++shiftPosition;
        }

        //Return if new widget is topmost
        if (shiftPosition == widgetContainer.size())
            return shiftPosition;

        //Start shifting in case of index conflict
        if (widgetContainer.get(shiftPosition).getIndex() == firstIndex) {
            int changingIndex = firstIndex;
            for (int position = shiftPosition; proceedShift(position, changingIndex); ++position)
                widgetContainer.get(position).setIndex(++changingIndex);
        }

        return shiftPosition;
    }

    private boolean proceedShift(final int position, final int index) {
        //Need to continue shifting process if index in current position (if exist) equals to index of previous/new widget
        return position < widgetContainer.size() && widgetContainer.get(position).getIndex() == index;
    }

    private int getWidgetPositionByID(UUID id) {
        for (int position = 0; position < widgetContainer.size(); ++position)
            if (widgetContainer.get(position).getId().equals(id))
                return position;
        return -1;
    }
}
