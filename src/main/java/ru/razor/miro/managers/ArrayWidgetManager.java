package ru.razor.miro.managers;

import org.springframework.stereotype.Component;
import ru.razor.miro.entities.Widget;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Component
public class ArrayWidgetManager implements WidgetManager {
    @Override
    public Widget addWidget(int x, int y, int width, int height, int index) {
        return new Widget(UUID.randomUUID(), x, y, index, width, height, LocalDateTime.now());
    }

    @Override
    public Widget changeWidget(UUID id, int x, int y, int width, int height, int index) {
        return new Widget(id, x, y, index, width, height, LocalDateTime.now());
    }

    @Override
    public void deleteWidget(UUID id) {

    }

    @Override
    public Widget getWidgetById(UUID id) {
        Random random = new Random();
        return new Widget(id, random.nextInt(), random.nextInt(), random.nextInt(), Math.abs(random.nextInt()), Math.abs(random.nextInt()), LocalDateTime.now());
    }

    @Override
    public List<Widget> getWidgetList() {
        return List.of(new Widget(UUID.randomUUID(), 1, 2, 3, 4, 5, LocalDateTime.now())
                        , new Widget(UUID.randomUUID(), 6, 7, 8, 9, 10, LocalDateTime.now())
                        , new Widget(UUID.randomUUID(), 11, 12, 13, 14, 15, LocalDateTime.now()));
    }
}
