package com.example.demo.service;

import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import com.example.demo.entity.Account;
import com.example.demo.entity.Role;
import com.example.demo.model.AuthenticationRequest;
import com.example.demo.model.AuthenticationResponse;
import com.example.demo.model.MyUserDetails;
import com.example.demo.model.RegisterRequest;
import com.example.demo.repository.AccountRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenUtil jwtService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		var account = accountRepository.findByUsername(username);
		if (account == null) {
			throw new UsernameNotFoundException("Could not find username");
		}
		return new MyUserDetails(account);
	}

	public AuthenticationResponse createAccount(RegisterRequest a) {
		Account getAccount = accountRepository.findByUsername(a.getUsername());
		Assert.isNull(getAccount, "Account existed!");
		
		a.setPassword(passwordEncoder.encode(a.getPassword()));
		
		Role role = new Role();
		if(a.getUsername().equalsIgnoreCase("admin")) {
			role.setRole_name("ADMIN");
		}else {
			role.setRole_name("USER");
		}
		//System.out.println(role);
		Set<Role> setRole = new HashSet<>();
		setRole.add(role);
		var account = Account.builder()
				.fullname(a.getFullname())
				.sex(a.getSex())
				.username(a.getUsername())
				.password(a.getPassword())
				.roles(setRole)
				.build();
		var jwtToken = jwtService.generateToken(account.getUsername());
		accountRepository.save(account);
		return AuthenticationResponse.builder()
				.status("OK")
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest a) {
		var account = accountRepository.findByUsername(a.getUsername());
		if (account == null) {
			return AuthenticationResponse.builder()
					.status("Account not exist")
					.token(null)
					.build();
		}
	
		System.out.println(passwordEncoder.matches(a.getPassword(), account.getPassword()));
		if(!passwordEncoder.matches(a.getPassword(), account.getPassword())){
			return AuthenticationResponse.builder()
					.status("Wrong password")
					.token(null)
					.build();
		}
		var token = jwtService.generateToken(a.getUsername());
		return AuthenticationResponse.builder()
				.status("OK")
				.token(token)
				.build();
	}

}
