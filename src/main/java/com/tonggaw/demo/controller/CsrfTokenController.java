package com.tonggaw.demo.controller;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/csrfApi")
public class CsrfTokenController {


    @GetMapping("/getCsrf")
    public String getCsrfToken(CsrfToken token) {
        return token.getToken();
    }

}
