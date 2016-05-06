package com.golfapp.test.datafiles;

import com.orm.SugarRecord;

/**
 * Created by aman on 11/26/2015.
 */
public class RestaurantMenuData extends SugarRecord<RestaurantMenuData> {

    public int restaurantID;
    public int rateID;
    public int itemID;
    public int isSection;
    public String section;
    public String descr;
    public String price;


    public RestaurantMenuData() {

    }

    public RestaurantMenuData(int restaurantID, int rateID, int itemID, int isSection, String section, String descr, String price) {
        this.restaurantID = restaurantID;
        this.isSection = isSection;
        this.rateID = rateID;
        this.itemID = itemID;
        this.section = section;
        this.descr = descr;
        this.price = price;
    }
}
