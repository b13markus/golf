package com.golfapp.test.datafiles;

import com.orm.SugarRecord;

/**
 * Created by aman on 11/24/2015.
 */
public class CourseRateData extends SugarRecord<CourseRateData> {

    public int courseID;
    public int rateID;
    public int itemID;
    public int isSection;
    public String section;
    public String descr;
    public String price;


    public CourseRateData() {

    }

    public CourseRateData(int courseID, int rateID, int itemID, int isSection, String section, String descr, String price) {
        this.courseID = courseID;
        this.isSection = isSection;
        this.rateID = rateID;
        this.itemID = itemID;
        this.section = section;
        this.descr = descr;
        this.price = price;
    }
}
