package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Account;
import com.example.demo.model.AuthenticationRequest;
import com.example.demo.model.AuthenticationResponse;
import com.example.demo.model.RegisterRequest;
import com.example.demo.repository.AccountRepository;
import com.example.demo.service.AccountService;
import com.example.demo.service.JwtTokenUtil;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
public class AccountController {

	@Autowired
	public AccountRepository accountRepository;
	
	@Autowired
	public JwtTokenUtil jwtTokenUtil;

	@Autowired
	public AccountService accountService;

	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> login(@NonNull @RequestBody AuthenticationRequest request) {
		AuthenticationResponse response = accountService.authenticate(request);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/create")
	public ResponseEntity<AuthenticationResponse> create(@NonNull @RequestBody RegisterRequest request) {
		//System.out.println("request="+request);
		AuthenticationResponse response = accountService.createAccount(request);
		return ResponseEntity.ok(response);
	}

	@GetMapping("/home")
	public String inform(@RequestHeader("Authorization") String token) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Account a = (Account) authentication.getPrincipal();
//		//System.out.println(a);
		System.out.println(token);
		return "Thành công!";
	}

	@GetMapping("/admin")
	public String pageAdmin(@RequestHeader("Authorization") String token) {
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Account a = (Account) authentication.getPrincipal();
//		System.out.println(a);
		System.out.println(token);
		return "Hello admin!";
	}

	@GetMapping("/user")
	public String pageUser(@RequestHeader("Authorization") String token) {
		System.out.println("token:"+token);
//		String username = jwtTokenUtil.getUsernameFromToken(token);
//		if(jwtTokenUtil.validateToken(token, accountService.loadUserByUsername(username)))
//		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//		Account a = (Account) authentication.getPrincipal();
//		System.out.println(a);
		return "Hello client!";
		//return "Expired!";
	}

}
