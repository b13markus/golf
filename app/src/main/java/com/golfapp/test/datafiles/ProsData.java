package com.golfapp.test.datafiles;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aman on 11/21/2015.
 */
public class ProsData extends SugarRecord {

    public int prosID;
    public String name;
    public String desc;
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
    public double longitude;
    public double latitude;

    public int position;
    public int package_count;
    public String package_url;

    @Ignore
    public List<ImageData> imageList = new ArrayList<>();

    public ProsData() {

    }

    public ProsData(int prosID, int position, int package_count, double longitude, double latitude,
                    String name, String desc, String phone, String email, String website, String address,
                    String streetno, String route, String city, String state, String postalcode, String country,
                    String package_url) {

        this.prosID = prosID;
        this.position = position;
        this.package_count = package_count;
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
        this.desc = desc;
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
        this.package_url = package_url;

    }
}
