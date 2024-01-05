package com.example.demo.model;

import org.springframework.context.annotation.Lazy;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Builder
@Data
public class AuthenticationRequest {

	private String username;
	private String password;
}
