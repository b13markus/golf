package com.golfapp.test.datafiles;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

/**
 * Created by aman on 11/21/2015.
 */
public class ImageData extends SugarRecord<ImageData> {

    public int prosID;
    public int newsID;
    public int courseID;
    public int hotelID;
    public int proShopID;
    public int restaurantID;
    public String name;
    public String url;
    @Ignore
    public boolean isLoaded = false;

    public ImageData() {

    }

    public ImageData(int prosID, int newsID, int courseID, int restaurantID, int hotelID, int proShopID, String name, String url) {
        this.prosID = prosID;
        this.restaurantID = restaurantID;
        this.courseID = courseID;
        this.newsID = newsID;
        this.proShopID = proShopID;
        this.hotelID = hotelID;
        this.name = name;
        this.url = url;
    }


}
