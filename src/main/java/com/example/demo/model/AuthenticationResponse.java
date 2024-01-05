package com.example.demo.model;
import org.springframework.context.annotation.Lazy;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Builder
@Data
public class AuthenticationResponse{
	private String status;
	private String token;
}
