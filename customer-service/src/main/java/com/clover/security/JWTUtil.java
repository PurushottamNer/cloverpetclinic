package com.clover.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

@Component
public class JWTUtil {

	@Value("${jwt.secretKey}")
	private String secretKeyString;

	private SecretKey secretKey;

	@PostConstruct
	public void initialize() {
		this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
	}

	/**
	 * Generate a JWT token for the given username.
	 *
	 * @param username the username for which to generate the token
	 * @return the generated JWT token
	 */
	public String generateToken(String username) {
		long expirationTime = 1000L * 60 * 60 * 24; // 1 day in milliseconds
		Date expirationDate = new Date(System.currentTimeMillis() + expirationTime);

		return Jwts.builder().setSubject(username).setIssuedAt(new Date()).setExpiration(expirationDate)
				.signWith(secretKey, SignatureAlgorithm.HS256).compact();
	}

	/**
	 * Extract the email (subject) from the token.
	 *
	 * @param token the JWT token
	 * @return the email (subject)
	 */
	public String getEmailFromToken(String token) {
		return extractClaims(token).getSubject();
	}

	/**
	 * Extract the username (subject) from the token.
	 *
	 * @param token the JWT token
	 * @return the username (subject)
	 */
	public String extractUsername(String token) {
		return extractClaims(token).getSubject();
	}

	/**
	 * Validate the JWT token.
	 *
	 * @param token the JWT token
	 * @return true if the token is valid, false otherwise
	 */
	public boolean validateToken(String token) {
		try {
			extractClaims(token);
			return true;
		} catch (JwtException | IllegalArgumentException e) {
			System.err.println("Invalid JWT token: " + e.getMessage());
			return false;
		}
	}

	/**
	 * Extract claims from the JWT token.
	 *
	 * @param token the JWT token
	 * @return the claims contained in the token
	 * @throws SecurityException if the token signature is invalid
	 */
	public Claims extractClaims(String token) {
		try {
			return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
		} catch (JwtException e) {
			throw new SecurityException("JWT signature verification failed: " + e.getMessage(), e);
		}
	}
}
