package com.rest.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Booking {

//    {
//        "firstname" : "Ahmet",
//            "lastname" : "Topçu",
//            "totalprice" : 791,
//            "depositpaid" : true,
//            "bookingdates" : {
//        "checkin" : "2018-01-01",
//                "checkout" : "2019-01-01"
//    },
//        "additionalneeds" : "Breakfast"
//    }

    private String firstname;
    private String lastname;
    private Integer totalprice;
    private Boolean depositpaid;

}
