package org.example.controller;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import org.example.feignclient.NotificationFeignClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api/circuit-breaker-test")
public class CircuitBreakerTestController {

    private NotificationFeignClient notificationFeignClient;

    @Autowired
    public CircuitBreakerTestController(NotificationFeignClient notificationFeignClient) {
        this.notificationFeignClient = notificationFeignClient;
    }

    @Operation(summary = "Демонстрация работы Circuit Breaker")
    @GetMapping
    @CircuitBreaker(name = "notificationService",
            fallbackMethod = "fallbackMessageFromNotificationService")
    public String readMessageFromNotificationService() throws TimeoutException {
        return notificationFeignClient.testForCircuitBreaker();
    }

    private String fallbackMessageFromNotificationService(Throwable t) {
        return "Альтернативная реализация метода";
    }
}
