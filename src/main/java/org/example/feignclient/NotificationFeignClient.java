package org.example.feignclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.concurrent.TimeoutException;

@FeignClient("notification-service")
public interface NotificationFeignClient {

    @RequestMapping(
            method = RequestMethod.GET,
            value = "/api/email/test-for-circuit-breaker"
    )
    String testForCircuitBreaker() throws TimeoutException;
}
