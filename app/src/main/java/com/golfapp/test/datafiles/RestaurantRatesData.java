package com.golfapp.test.datafiles;

import com.orm.SugarRecord;

/**
 * Created by aman on 11/26/2015.
 */
public class RestaurantRatesData extends SugarRecord {
    public int restaurantID;
    public int rateID;
    public String name;
    public String desc;
    public String subTitle;
    public String pubDate;

    public RestaurantRatesData() {

    }

    public RestaurantRatesData(int restaurantID, int rateID, String name, String desc, String subTitle, String pubDate) {
        this.restaurantID = restaurantID;
        this.rateID = rateID;
        this.name = name;
        this.desc = desc;
        this.subTitle = subTitle;
        this.pubDate = pubDate;

    }


}
