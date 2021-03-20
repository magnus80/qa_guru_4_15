package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static utils.FileUtils.readStringFromFile;

class RegressInTests {

    @BeforeAll
    static void setup() {
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://reqres.in/";
    }

    @Test
    void creatingUserTest() {
        given()
                .body(readStringFromFile("src/test/resources/created_user.txt"))
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .body("id", is(notNullValue()));
    }

    @Test
    void gettingFirstUserTest() {
        Response response = given()
                .when()
                .get("/api/users/1")
                .then()
                .statusCode(200)
                .extract().response();

        assertThat(response.jsonPath().get("data.first_name"), is("George"));
    }

    @Test
    void gettingUserListTest() {
        Response response = given()
                .when()
                .get("/api/users")
                .then()
                .statusCode(200)
                .log().body()
                .extract().response();

        assertThat(response.jsonPath().getList("data.last_name", String.class), hasItem("Bluth"));
    }

    @Test
    void registeringWrongUserTest() {
        given().contentType(JSON)
                .when()
                .post("/api/register")
                .then()
                .statusCode(400)
                .body("error", is("Missing email or username"));
    }

    @Test
    void deletingUserTest() {
        given().contentType(JSON)
                .when()
                .delete("/api/users/2")
                .then()
                .statusCode(204);
    }
}
