package tn.esprit.scheduling.controller;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scheduling")
public class HelloController {

    @Value("${spring.application.name}")
    private String serviceName;

    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
                "message", "Hello from Scheduling Microservice (MS7)",
                "service", serviceName,
                "timestamp", LocalDateTime.now().toString());
    }
}
