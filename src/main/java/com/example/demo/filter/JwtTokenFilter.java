package com.example.demo.filter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.demo.service.AccountService;
import com.example.demo.service.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter{

	@Autowired
	 private JwtTokenUtil jwtService;
	
	@Autowired
	 private AccountService accountService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
		final String jwt,username;
		System.out.println("authHeader="+authHeader);
		if(authHeader == null || !authHeader.startsWith("Bearer ")) {
			filterChain.doFilter(request, response);
			return;
		}
		jwt = authHeader.substring(7);
		username = jwtService.getUsernameFromToken(jwt);
		      
		 if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			 UserDetails userDetails = this.accountService.loadUserByUsername(username);
			 if(jwtService.validateToken(jwt,userDetails)) { //Vừa để username thay vì jwt
				 UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
						 userDetails
						 ,null
						 ,userDetails.getAuthorities()
						 );
				 authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); 
				 /*
		           * After setting the Authentication in the context, we specify that the current user is
		           * authenticated. So it passes the Spring Security Configurations successfully
		           * */
		          SecurityContextHolder.getContext().setAuthentication(authToken);
			 }
		 }
		   filterChain.doFilter(request, response);
	}
	  
	  
}
