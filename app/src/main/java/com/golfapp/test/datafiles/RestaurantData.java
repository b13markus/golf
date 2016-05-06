package com.golfapp.test.datafiles;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by aman on 11/26/2015.
 */
public class RestaurantData extends SugarRecord<RestaurantData> {

    public int restaurantID;
    public int position;
    public int packageCount;
    public int menuCount;
    public String name;
    public String descr;
    public String phone;
    public String email;
    public String website;
    public String address;
    public String streetno;
    public String route;
    public String city;
    public String state;
    public String postalcode;
    public String country;
    public String packageUrl;
    public String menuUrl;
    public double latitude;
    public double longitude;

    @Ignore
    public List<ImageData> imageList = new ArrayList<>();

    public RestaurantData() {

    }

    public RestaurantData(int restaurantID, int position, int packageCount, int menuCount,
                          String name, String descr, String phone, String email, String website, String address,
                          String streetno, String route, String city, String state, String postalcode, String country,
                          String packageUrl, String menuUrl, double latitude, double longitude) {

        this.restaurantID = restaurantID;
        this.position = position;
        this.packageCount = packageCount;
        this.menuCount = menuCount;
        this.name = name;
        this.descr = descr;
        this.phone = phone;
        this.email = email;
        this.website = website;
        this.address = address;
        this.streetno = streetno;
        this.route = route;
        this.city = city;
        this.state = state;
        this.postalcode = postalcode;
        this.country = country;
        this.packageUrl = packageUrl;
        this.menuUrl = menuUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
