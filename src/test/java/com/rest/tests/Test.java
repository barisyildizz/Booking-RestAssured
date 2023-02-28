package com.rest.tests;

import com.rest.pojo.Booking;
import com.rest.pojo.Error;
import com.rest.pojo.Token;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;

import static com.rest.builder.SpecBuilder.getRequestSpec;
import static com.rest.builder.SpecBuilder.getResponseSpec;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Test {
    String tooken;
    Integer bookingID;
    Token tok;


    @org.testng.annotations.Test(priority = 1)
    public void createToken() {
        tok = new Token();
        tok.setUsername("admin");
        tok.setPassword("password123");

//        String x = "{\n" +
//                "    \"username\" : \"admin\",\n" +
//                "    \"password\" : \"password123\"\n" +
//                "}";

        Response response = RestAssured.given(getRequestSpec()).
                body(tok).
                when().
                post("/auth").
                then().
                spec(getResponseSpec()).
                assertThat().
                contentType(ContentType.JSON).
                statusCode(200).
                extract().response();

        tooken = response.path("token");
    }

    @org.testng.annotations.Test(priority = 2)
    public void canNotCreateToken() {
        tok = new Token();
        tok.setUsername("admin");
        tok.setPassword("password12344");

        Error er = new Error();
//        String x = "{\n" +
//                "    \"username\" : \"admin\",\n" +
//                "    \"password\" : \"password123\"\n" +
//                "}";

        Error response = RestAssured.given(getRequestSpec()).
                body(tok).
                when().
                post("/auth").
                then().
                spec(getResponseSpec()).
                assertThat().
                //   body("reason",equalTo("Bad credentials")).
                        contentType(ContentType.JSON).
                statusCode(200).
                extract().as(Error.class);

        Assert.assertEquals("Bad credentials", response.getReason());

        // tooken = response.path("token");

        // String errorCode = response.path("reason");
        //Assert.assertEquals(errorCode, "Bad credentials");


    }


    @org.testng.annotations.Test(priority = 3)
    public void createBooking() {

        String payload = "{\n" +
                "    \"firstname\" : \"Ahmet\",\n" +
                "    \"lastname\" : \"Topçu\",\n" +
                "    \"totalprice\" : 791,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        Response response = given(getRequestSpec()).
                contentType(ContentType.JSON).
                header("accept", "application/json").
                when().
                body(payload).
                post("/booking").
                then().
                statusCode(200).
                extract().
                response();

        bookingID = response.path("bookingid");
        System.out.println(bookingID.toString());

    }

    @org.testng.annotations.Test(priority = 4)
    public void getBooking() {

        given(getRequestSpec()).
                when().
                get("/booking/" + bookingID).
                then().
                spec(getResponseSpec()).
                assertThat().
                statusCode(200).
                body("firstname", equalTo("Ahmet"),
                        "lastname", equalTo("Topçu"),
                        "totalprice", equalTo(791),
                        "depositpaid", equalTo(true),
                        "bookingdates.checkin", equalTo("2018-01-01"),
                        "bookingdates.checkout", equalTo("2019-01-01"),
                        "additionalneeds", equalTo("Breakfast")
                );


    }


    @org.testng.annotations.Test(priority = 5)
    public void patchBooking() {

        Booking booking = new Booking();
        booking.setFirstname("Ahmet");
        booking.setLastname("Duman");

//        String payload = "{\n" +
//                "    \"firstname\" : \"Ahmet\",\n" +
//                "    \"lastname\" : \"Duman\"\n" +
//                "}";

        given(getRequestSpec()).
                contentType(ContentType.JSON).
                header("accept", "application/json").
                header("Cookie", "token=" + tooken).
                when().
                body(booking).
                patch("/booking/23").
                then().
                assertThat().statusCode(200).
                log().all();
    }


}
