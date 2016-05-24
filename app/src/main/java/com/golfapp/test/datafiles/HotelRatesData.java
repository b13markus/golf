package com.golfapp.test.datafiles;

import com.orm.SugarRecord;

/**
 * Created by aman on 11/26/2015.
 */
public class HotelRatesData extends SugarRecord {

    public int hotelID;
    public int rateID;
    public String name;
    public String desc;
    public String subTitle;
    public String pubDate;

    public HotelRatesData() {

    }

    public HotelRatesData(int hotelID, int rateID, String name, String desc, String subTitle, String pubDate) {
        this.hotelID = hotelID;
        this.rateID = rateID;
        this.name = name;
        this.desc = desc;
        this.subTitle = subTitle;
        this.pubDate = pubDate;

    }

}
