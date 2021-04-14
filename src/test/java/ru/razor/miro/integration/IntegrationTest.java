package ru.razor.miro.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import ru.razor.miro.controllers.WidgetTestController;
import ru.razor.miro.dto.WidgetDTO;
import ru.razor.miro.managers.entities.TestWidgetEntity;

import java.io.File;
import java.net.URL;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private WidgetTestController controller;

    private List<TestWidgetEntity> testWidgetEntities;

    private final List<UUID> idList = new ArrayList<>();

    private final RestTemplate restTemplate = new RestTemplate();

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
    }

    @Test
    public void fullScenario() {
        for (TestWidgetEntity entity: testWidgetEntities)
            sendCreateAndCheck(entity);

        String url = "http://localhost:"
                + port
                + "/change?id="
                + idList.get(1)
                + "&x=1&y=2&width=3&height=4";
        ResponseEntity<WidgetDTO> response = restTemplate.postForEntity(url
                , null
                , WidgetDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(idList.get(1), response.getBody().getId());
        assertEquals(1, response.getBody().getX());

        url = "http://localhost:"
                + port
                + "/delete?id="
                + idList.get(2);

        ResponseEntity<Void> responseNoBody = restTemplate.postForEntity(url
                , null
                , Void.class);

        assertEquals(HttpStatus.OK, responseNoBody.getStatusCode());

        ResponseEntity<List<WidgetDTO>> responseList = restTemplate.exchange("http://localhost:" + port + "/getList"
                , HttpMethod.POST
                , null
                , new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseList.getBody());
        assertEquals(4, responseList.getBody().size());

        assertEquals(response.getBody(), responseList.getBody().get(0));

        assertEquals(idList.get(0), responseList.getBody().get(1).getId());
        assertEquals(idList.get(1), responseList.getBody().get(0).getId());
        assertEquals(idList.get(3), responseList.getBody().get(2).getId());
        assertEquals(idList.get(4), responseList.getBody().get(3).getId());
    }

    private void sendCreateAndCheck(TestWidgetEntity entity) {
        String url = "http://localhost:"
                + port
                + "/create?x="
                + entity.getX()
                + "&y="
                + entity.getY()
                + "&width="
                + entity.getWidth()
                + "&height="
                + entity.getHeight()
                + (entity.getIndex() == null? "" : "&index=" + entity.getIndex());

        ResponseEntity<WidgetDTO> response = restTemplate.postForEntity(url
                , null
                , WidgetDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(entity.getX(), response.getBody().getX());
        assertEquals(entity.getY(), response.getBody().getY());
        assertEquals(entity.getWidth(), response.getBody().getWidth());
        assertEquals(entity.getHeight(), response.getBody().getHeight());

        idList.add(response.getBody().getId());
    }
}
