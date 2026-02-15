package com.restaurants.demo.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurants.demo.dto.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request,
						 HttpServletResponse response,
						 AuthenticationException authException)
			throws IOException {

		response.setContentType("application/json");
		response.setStatus(HttpStatus.UNAUTHORIZED.value());

		ErrorResponse error = new ErrorResponse(
				HttpStatus.UNAUTHORIZED.value(),
				"Unauthorized - Please login"
		);

		new ObjectMapper().writeValue(response.getOutputStream(), error);
	}
}
