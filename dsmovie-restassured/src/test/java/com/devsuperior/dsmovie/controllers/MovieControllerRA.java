package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private String movieName;
	private Long existingMovieId, nonExistingMovieId;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String adminToken, clientToken, invalidToken;
	
	private Map<String, Object> postMovieInstance;
	
	@BeforeEach
	void setUp() throws Exception {
		
		baseURI = "http://localhost:8080";

		existingMovieId = 1L;
		nonExistingMovieId = 100L;
		
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = adminToken + "invalidate.token";
		
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", 4.0);
		postMovieInstance.put("count", 1);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		
		given().get("/movies").then().statusCode(200);

	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {
		
		movieName = "vingadores";
		
		// @formatter:off
		given()
			.get("/movies?title={movieName}", movieName)
		.then()
			.statusCode(200)
			.body("content.title", hasItems("Vingadores: Ultimato"))
			.body("content.id[0]", is(13))
			.body("content.title[0]", equalTo("Vingadores: Ultimato"));
		// @formatter:on		
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {
		
		// @formatter:off
		given()
			.get("/movies/{id}", existingMovieId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("title", equalTo("The Witcher"))
			.body("score", is(4.5F))
			.body("count", is(2))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
		// @formatter:on
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {
		
		// @formatter:off
		given()
			.get("/movies/{id}", nonExistingMovieId)
		.then()
			.statusCode(404);
		// @formatter:on
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {
		
		postMovieInstance.put("title", "     ");
		JSONObject newMovie = new JSONObject(postMovieInstance);
		// @formatter:off
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(422);
		// @formatter:on
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		
		JSONObject newMovie = new JSONObject(postMovieInstance);
		// @formatter:off
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
		// @formatter:on
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		
		JSONObject newMovie = new JSONObject(postMovieInstance);
		// @formatter:off
		given()
			.header("Content-Type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
		// @formatter:on
	}
}
