package ru.razor.miro.managers;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.razor.miro.dto.WidgetDTO;
import ru.razor.miro.managers.entities.TestWidgetEntity;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class ArrayWidgetManagerTest {

    private List<TestWidgetEntity> testWidgetEntities;

    @Autowired
    private ArrayWidgetManager manager;

    @BeforeEach
    void setupTest(){
        try {
            URL jsonUrl = Objects.requireNonNull(getClass().getClassLoader().getResource("testWidgets.json"));
            File json = new File(jsonUrl.toURI());
            ObjectMapper mapper = new ObjectMapper();
            testWidgetEntities = Arrays.asList(mapper.readValue(json, TestWidgetEntity[].class));
        }
        catch (Exception e) {
            log.error("Could not read values from file {}", e.getMessage());
        }

        manager.clearList();
    }

    @Test
    void testAddGetList() {
        for (TestWidgetEntity entity: testWidgetEntities) {
            manager.addWidget(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight(), entity.getIndex());
        }

        List<WidgetDTO> result = manager.getWidgetList();

        assertEquals(5, result.size());

        assertEquals(20, result.get(0).getX());
        assertEquals(21, result.get(0).getY());
        assertEquals(100, result.get(0).getWidth());
        assertEquals(50, result.get(0).getHeight());
        assertEquals(1, result.get(0).getIndex());
        assertNotNull(result.get(0).getModified());

        assertEquals(30, result.get(1).getX());
        assertEquals(3, result.get(1).getIndex());

        assertEquals(10, result.get(2).getX());
        assertEquals(4, result.get(2).getIndex());

        assertEquals(40, result.get(3).getX());
        assertEquals(5, result.get(3).getIndex());

        assertEquals(50, result.get(4).getX());
        assertEquals(7, result.get(4).getIndex());
    }

    @Test
    void testChangeWidget() {
        for (TestWidgetEntity entity: testWidgetEntities) {
            manager.addWidget(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight(), entity.getIndex());
        }

        List<WidgetDTO> result = manager.getWidgetList();

        try {
            Thread.sleep(500);
        }
        catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }

        manager.changeWidget(result.get(0).getId(), 22, 23, 101, 51, 1);
        manager.changeWidget(result.get(2).getId(), 12, 13, 51, 101, 7);

        List<WidgetDTO> resultChanged = manager.getWidgetList();

        assertEquals(5, resultChanged.size());

        assertEquals(result.get(0).getId(), resultChanged.get(0).getId());
        assertEquals(22, resultChanged.get(0).getX());
        assertEquals(23, resultChanged.get(0).getY());
        assertEquals(101, resultChanged.get(0).getWidth());
        assertEquals(51, resultChanged.get(0).getHeight());
        assertEquals(1, resultChanged.get(0).getIndex());
        assertTrue(resultChanged.get(0).getModified().isAfter(result.get(0).getModified()));

        assertEquals(result.get(1), resultChanged.get(1));

        assertEquals(result.get(3), resultChanged.get(2));

        assertEquals(result.get(2).getId(), resultChanged.get(3).getId());
        assertEquals(7, resultChanged.get(3).getIndex());
        assertEquals(12, resultChanged.get(3).getX());

        assertEquals(result.get(4).getId(), resultChanged.get(4).getId());
        assertEquals(8, resultChanged.get(4).getIndex());
    }

    @Test
    void testGetWidgetById() {
        for (TestWidgetEntity entity: testWidgetEntities) {
            manager.addWidget(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight(), entity.getIndex());
        }

        List<WidgetDTO> result = manager.getWidgetList();

        WidgetDTO dto = manager.getWidgetById(result.get(1).getId());

        assertEquals(result.get(1), dto);
    }

    @Test
    void testDeleteWidget() {
        for (TestWidgetEntity entity: testWidgetEntities) {
            manager.addWidget(entity.getX(), entity.getY(), entity.getWidth(), entity.getHeight(), entity.getIndex());
        }

        List<WidgetDTO> result = manager.getWidgetList();

        manager.deleteWidget(result.get(1).getId());

        List<WidgetDTO> resultChanged = manager.getWidgetList();

        assertEquals(4, resultChanged.size());

        assertEquals(result.get(0), resultChanged.get(0));

        assertEquals(result.get(2), resultChanged.get(1));

        assertEquals(result.get(3), resultChanged.get(2));

        assertEquals(result.get(4), resultChanged.get(3));

        try {
            manager.getWidgetById(result.get(1).getId());
        }
        catch (IndexOutOfBoundsException e) {
            return;
        }

        fail("Didn't get Index OoB exception");
    }

    @Test
    void testValidation() {
        ValidationException ex = assertThrows(ValidationException.class
                , () -> manager.addWidget(10, 10, -1, 0, 1));

        assertTrue(ex.getMessage().contains("Widget width must be positive"));
        assertTrue(ex.getMessage().contains("Widget height must be positive"));
    }
}