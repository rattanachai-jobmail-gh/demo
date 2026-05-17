package com.tonggaw.demo.security;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletResponse;

@ControllerAdvice
public class CsrfControllerAdvice {

	@ModelAttribute
	public void getCsrfToken(HttpServletResponse response, CsrfToken csrfToken) {
		System.out.println("CsrfControllerAdvice.getCsrfToken: " + csrfToken.getHeaderName());
		System.out.println("CsrfControllerAdvice.getCsrfToken: " + csrfToken.getToken());
		response.setHeader(csrfToken.getHeaderName(), csrfToken.getToken());
	}

}
