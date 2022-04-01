package com.interview;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Scanner;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.reset;

public class Countries {
    @BeforeAll
    public static void setup() {

        RestAssured.baseURI = "https://restcountries.com";
        RestAssured.basePath = "/v3.1";
    }

    @AfterAll
    public static void teardown() {
        reset();
    }


    /**
     * A) Using the provide REST service, create a program that returns, at
     * //minimum, capital city based on user input for name or code.
     **/
    public static void main(String[] args) {
        returnCapital();
    }

    public static String returnCapital() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("please provide the country name : ");

        String country = scanner.next();

        System.out.println("country = " + country);
        Response response = given()
                .accept(ContentType.JSON).
                when()
                .get("https://restcountries.com/v3.1/name/" + country);

        String capital = response.path("capital[0][0]");

        scanner.close();
        System.out.println("capital = " + capital);

        return capital;
    }


    /**
     * B) Write several tests that validate positive and negative scenario’s
     **/
/*
     Scenario: (Positive) when user provide correct country name and url should get statusCode == 200
 */
    @Test
    public void testCountries() {
        Response response = given().log().all()
                .pathParam("country", "usa")
                .accept(ContentType.JSON).
                when().get("/name/{country}");
        System.out.println("response.statusCode() = " + response.statusCode());

        Assertions.assertEquals(response.statusCode(), 200);

    }

    /*
    Scenario: (Positive)  when user provide correct country name the capital from response  should match with expected
     */
    @Test
    public void testCapital() {

        String expectedCapital = "Washington, D.C.";
        String country = "usa";
        Response response = given()
                .accept(ContentType.JSON).
                when().get("/name/" + country);

        String capital = response.path("capital[0][0]");
        System.out.println("capital = " + capital);
        Assertions.assertEquals(expectedCapital, capital);

    }

    /*
       Scenario: (Negative)  when user provide incorrect URI, the status code should be 404
        */
    @Test
    public void incorrectURL() {
        Response response = given().log().uri()
                .pathParam("country", "usa")
                .when().get("/n/{country}");  // 'n' instead of 'name'

        int actualStatusCode = response.statusCode();
        int expectedStatusCode = 404;
        Assertions.assertEquals(expectedStatusCode, actualStatusCode);
    }

    /*
    Scenario: (Positive ) when user  search countries by language and select 'english'  response has to contain 'United States'
     */
    @Test
    public void languageTest() {

        String expectedCountry = "United States";
        Response response = given().log().uri()
                           .pathParam("language", "english")
                           .when().get("/lang/{language}");

       ArrayList<String> allCountries =  response.path("name.common");
        System.out.println("allCountries = " + allCountries);

        Assertions.assertTrue(allCountries.contains(expectedCountry));

    }

    /*
       Scenario: (Negative ) when user  search countries by language and select 'english'  response should not contain 'China'
        */
    @Test
    public void languageTestNegative() {

        String expectedCountry = "China";
        Response response = given().log().uri()
                .pathParam("language", "english")
                .when().get("/lang/{language}");

        ArrayList<String> allCountries =  response.path("name.common");
        System.out.println("allCountries = " + allCountries);

        Assertions.assertFalse(allCountries.contains(expectedCountry));

    }

}
