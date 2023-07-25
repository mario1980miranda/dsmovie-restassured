package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {
	
	private Long existingMovieId, nonExistingMovieId;
	private String clientUsername, clientPassword;
	private String clientToken;
	
	private Map<String, Object> putScoreInstance;
	
	@BeforeEach
	void setUp() throws Exception {
		
		baseURI = "http://localhost:8080";
	
		existingMovieId = 1L;
		nonExistingMovieId = 100L;
		
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		
		putScoreInstance = new HashMap<>();
		putScoreInstance.put("movieId", existingMovieId);
		putScoreInstance.put("score", 4);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		
		putScoreInstance.put("movieId", nonExistingMovieId);
		JSONObject updateScore = new JSONObject(putScoreInstance);
		// @formatter:off
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(updateScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(404);
		// @formatter:on
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		
		putScoreInstance.put("movieId", null);
		JSONObject updateScore = new JSONObject(putScoreInstance);
		// @formatter:off
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(updateScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422);
		// @formatter:on
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		
		putScoreInstance.put("score", -4);
		JSONObject updateScore = new JSONObject(putScoreInstance);
		// @formatter:off
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(updateScore)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422);
		// @formatter:on
	}
}
