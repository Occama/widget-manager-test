package ru.razor.miro.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ru.razor.miro.dto.WidgetDTO;
import ru.razor.miro.managers.WidgetManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WidgetTestControllerTest {

    @MockBean
    private WidgetManager manager;

    @LocalServerPort
    private int port;

    @Autowired
    private WidgetTestController controller;

    private final WidgetDTO dto = WidgetDTO
            .builder()
            .id(UUID.randomUUID())
            .x(1)
            .y(2)
            .width(3)
            .height(4)
            .index(5)
            .modified(LocalDateTime.now())
            .build();

    private final RestTemplate restTemplate = new RestTemplate();

    @Test
    void create() {
        Mockito.when(manager.addWidget(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(dto);

        String url = "http://localhost:"
                        + port
                        + "/create?x=1&y=2&width=3&height=4";
        ResponseEntity<WidgetDTO> response = restTemplate.postForEntity(url
                                                            , null
                                                            , WidgetDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void change() {
        Mockito.when(manager.changeWidget(Mockito.any(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.any()))
                .thenReturn(dto);

        String url = "http://localhost:"
                        + port
                        + "/change?id="
                        + dto.getId()
                        + "&x=1&y=2&width=3&height=4";
        ResponseEntity<WidgetDTO> response = restTemplate.postForEntity(url
                                                            , null
                                                            , WidgetDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void delete() {
        String url = "http://localhost:"
                + port
                + "/delete?id="
                + dto.getId();

        ResponseEntity<Void> response = restTemplate.postForEntity(url
                , null
                , Void.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getById() {
        Mockito.when(manager.getWidgetById(Mockito.any())).thenReturn(dto);

        String url = "http://localhost:"
                + port
                + "/getById?id="
                + dto.getId();

        ResponseEntity<WidgetDTO> response = restTemplate.postForEntity(url
                , null
                , WidgetDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dto, response.getBody());
    }

    @Test
    void getList() {
        WidgetDTO anotherDto =WidgetDTO
                .builder()
                .id(UUID.randomUUID())
                .x(10)
                .y(11)
                .width(12)
                .height(13)
                .index(14)
                .modified(LocalDateTime.now())
                .build();

        List<WidgetDTO> list = new ArrayList<>();
        list.add(dto);
        list.add(anotherDto);

        Mockito.when(manager.getWidgetList()).thenReturn(list);

        ResponseEntity<List<WidgetDTO>> response = restTemplate.exchange("http://localhost:" + port + "/getList"
                                                                , HttpMethod.POST
                                                                , null
                                                                , new ParameterizedTypeReference<>() {
                });

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(dto, response.getBody().get(0));
        assertEquals(anotherDto, response.getBody().get(1));
    }

    @Test
    public void testNoWidget() {
        Mockito.when(manager.getWidgetById(Mockito.any())).thenThrow(IndexOutOfBoundsException.class);

        String url = "http://localhost:"
                + port
                + "/getById?id="
                + dto.getId();

        HttpClientErrorException ex = assertThrows(HttpClientErrorException.class,
                () -> restTemplate.postForEntity(url
                , null
                , WidgetDTO.class) );

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }
}