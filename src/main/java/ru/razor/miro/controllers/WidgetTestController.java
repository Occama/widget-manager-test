package ru.razor.miro.controllers;

import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.razor.miro.dto.WidgetDTO;
import ru.razor.miro.managers.WidgetManager;

import java.util.List;
import java.util.UUID;

@RestController
public class WidgetTestController {

    private final WidgetManager manager;

    @Autowired
    public WidgetTestController(WidgetManager manager) {
        this.manager = manager;
    }

    @PostMapping(path="create")
    public ResponseEntity<WidgetDTO> create(@RequestParam int x
                                        , @RequestParam int y
                                        , @RequestParam @Min(value = 1) int width
                                        , @RequestParam @Min(value = 1) int height
                                        , @RequestParam(required = false) Integer index) {
        return new ResponseEntity<>(manager.addWidget(x, y, width, height, index), HttpStatus.CREATED);
    }

    @PostMapping(path="change")
    public ResponseEntity<WidgetDTO> change(@RequestParam String id
                        , @RequestParam int x
                        , @RequestParam int y
                        , @RequestParam @Min(value = 1) int width
                        , @RequestParam @Min(value = 1) int height
                        , @RequestParam(required = false) Integer index) {
        return new ResponseEntity<>(manager.changeWidget(UUID.fromString(id), x, y, width, height, index), HttpStatus.OK);
    }

    @PostMapping(path="delete")
    public ResponseEntity<Void> delete(@RequestParam String id) {
        manager.deleteWidget(UUID.fromString(id));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping(path="getById")
    public ResponseEntity<WidgetDTO> getById(@RequestParam String id) {
        return new ResponseEntity<>(manager.getWidgetById(UUID.fromString(id)), HttpStatus.OK);
    }

    @PostMapping(path="getList")
    public ResponseEntity<List<WidgetDTO>> getList() {
        return new ResponseEntity<>(manager.getWidgetList(), HttpStatus.OK);
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No widget with requested id")
    @ExceptionHandler(IndexOutOfBoundsException.class)
    public void widgetNotFound() {
        // nothing to do
    }
}
