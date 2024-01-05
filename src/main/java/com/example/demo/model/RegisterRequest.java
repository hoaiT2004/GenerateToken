package com.example.demo.model;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
@Data
@Builder
public class RegisterRequest {

	private String fullname;
	private String sex;
	private String username;
	private String password;
}
