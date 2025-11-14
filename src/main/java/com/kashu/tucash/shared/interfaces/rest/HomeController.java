package com.kashu.tucash.shared.interfaces.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public ResponseEntity<Map<String, Object>> home() {
        return ResponseEntity.ok(Map.of(
                "name", "Kashu API",
                "version", "1.0.0",
                "description", "Personal Finance Management Platform",
                "documentation", "/swagger-ui.html",
                "status", "running"
        ));
    }
}
