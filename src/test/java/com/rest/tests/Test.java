package com.rest.tests;

import com.rest.pojo.Booking;
import com.rest.pojo.Error;
import com.rest.pojo.Token;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Stories;
import io.qameta.allure.Story;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;

import static com.rest.builder.SpecBuilder.getRequestSpec;
import static com.rest.builder.SpecBuilder.getResponseSpec;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Epic("Restful Booker API Automation")
public class Test {
    String tooken;
    Integer bookingID;
    Token tok;

    @Description("Bu test casede, kullanıcı ve şifre body bilgileri ile token oluşturulacaktır ve " +
            "tooken değerine atılıp diğer test caselerde kullanılacaktır.")
    @org.testng.annotations.Test(priority = 1, description = "Should be able to create a Token with valid data")
    @Story("Book a reservation")
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

    @io.qameta.allure.Description("Bu test casede yanlış kullanıcı adı ve şifre denenerek token oluşuturulamadığı," +
            " hata mesajı görüldüğü doğrulanacaktır.")
    @org.testng.annotations.Test(priority = 2, description = "Should not be able to create Token with invalid data")
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


    @org.testng.annotations.Test(priority = 3, description = "Should be able to create booking")
    @Story("Book a reservation")
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

    @org.testng.annotations.Test(priority = 4, description = "Should be able to see booking details")
    @Story("Book a reservation")
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


    @org.testng.annotations.Test(priority = 5, description = "Should be able to update some part of the booking info")
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
