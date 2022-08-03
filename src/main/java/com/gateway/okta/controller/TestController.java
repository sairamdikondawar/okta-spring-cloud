package com.gateway.okta.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class TestController {

    @GetMapping({"/test","/testnew"})
    public Mono<String> getTest()
    {
//        final String hello = "hello";
        return Mono.fromCallable(() -> "Hello World !");
    }
}
