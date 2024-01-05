package com.example.demo.service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenUtil {

	@Value("${jwt.validity}")
	private Long tokenValidity;

//	@Value("${jwt.secret}")  => Quá ngắn kh đủ 256 bit
//	private String secret;
	
	// Tạo một khóa bí mật an toàn cho HS256(Tương đương cái secret)
	private SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	// For retrieving any information from token we will need the secret key
	private Claims getAllClaimsFromToken(String token) {
		return Jwts
				.parser()
				.setSigningKey(secret)
				.parseClaimsJws(token)
				.getBody();
	}

	/*
	 * Hàm này sẽ trả về một giá trị kiểu T được lấy từ JWT bằng cách sử dụng một
	 * hàm claimsResolver. Hàm này sẽ gọi hàm getAllClaimsFromToken để lấy đối tượng
	 * Claims trước khi áp dụng hàm claimsResolver.
	 */
	private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	// Retrieve username from JWT token
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	 // Generate token for username
	public String generateToken(String subject) {
		return doGenerateToken(new HashMap<>(), subject);
	}

	public String generateToken(
			Map<String, Object> extraClaims, UserDetails userDetails
	) {
		return doGenerateToken(extraClaims, userDetails.getUsername());
	}

	/*
	 * While creating the token - 1. Define claims of the token, like Issuer,
	 * Expiration, Subject, and the ID 2. Sign the JWT using the HS512 algorithm and
	 * secret key. 3. According to JWS Compact Serialization
	 * (https://tools.ietf.org/html/draft-ietf-jose-json-web-signature-41#section-3.
	 * 1) compaction of the JWT to a URL-safe string
	 */
	private String doGenerateToken(Map<String, Object> claims, String subject) {
		return Jwts.builder()
				.setClaims(claims)
				.setSubject(subject)
				.signWith(SignatureAlgorithm.HS256, secret)
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + tokenValidity * 1000))
				.compact();
	}

	// Validate token
	public Boolean validateToken(String token, UserDetails userDetails) {
		final String username = getUsernameFromToken(token);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
	}

	// Check if the token has expired
	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	// Get expired date
	private Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

}
