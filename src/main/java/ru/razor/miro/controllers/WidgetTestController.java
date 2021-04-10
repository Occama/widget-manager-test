package ru.razor.miro.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.razor.miro.entities.Widget;
import ru.razor.miro.managers.WidgetManager;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.UUID;

@RestController
public class WidgetTestController {

    private WidgetManager manager;

    @Autowired
    public WidgetTestController(WidgetManager manager) {
        this.manager = manager;
    }

    @PostMapping(path="create")
    public ResponseEntity<Widget> create(@RequestParam int x
                                        , @RequestParam int y
                                        , @RequestParam @Min(value = 1) int width
                                        , @RequestParam @Min(value = 1) int height
                                        , @RequestParam(required = false) Integer index) {
        return new ResponseEntity<>(manager.addWidget(x, y, width, height, index), HttpStatus.CREATED);
    }

    @PostMapping(path="change")
    public ResponseEntity<Widget> change(@RequestParam String id
                        , @RequestParam int x
                        , @RequestParam int y
                        , @RequestParam @Min(value = 1) int width
                        , @RequestParam @Min(value = 1) int height
                        , @RequestParam(required = false) Integer index) {
        return new ResponseEntity<>(manager.changeWidget(UUID.fromString(id), x, y, width, height, index), HttpStatus.OK);
    }

    @PostMapping(path="delete")
    public ResponseEntity delete(@RequestParam String id) {
        manager.deleteWidget(UUID.fromString(id));
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping(path="getById")
    public ResponseEntity<Widget> getById(@RequestParam String id) {
        return new ResponseEntity<>(manager.getWidgetById(UUID.fromString(id)), HttpStatus.OK);
    }

    @PostMapping(path="getList")
    public ResponseEntity<List<Widget>> getList() {
        return new ResponseEntity<>(manager.getWidgetList(), HttpStatus.OK);
    }
}
