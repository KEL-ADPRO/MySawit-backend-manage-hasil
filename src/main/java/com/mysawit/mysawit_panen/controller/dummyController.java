package com.mysawit.mysawit_panen.controller;

import com.mysawit.mysawit_panen.model.dummyModel;
import com.mysawit.mysawit_panen.service.dummyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "*")

public class dummyController {
    private final dummyService dummyService;
    public dummyController(dummyService dummyService) {
        this.dummyService = dummyService;
    }

    @PostMapping("/create")
    public dummyModel create() {
        return dummyService.create();
    }

    @GetMapping("/hello")
    public String hello() {
        return "backend connected successfully";
    }
}
