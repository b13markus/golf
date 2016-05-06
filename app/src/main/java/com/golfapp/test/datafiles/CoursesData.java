package com.golfapp.test.datafiles;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by aman on 11/24/2015.
 */
public class CoursesData extends SugarRecord<CoursesData> {

    public int courseID;
    public int position;
    public int rateCount;
    public String name;
    public String descr;
    public String holes;
    public String par;
    public String length;
    public String facilities;
    public String lengthUnit;
    public String rateUrl;

    @Ignore
    public List<com.golfapp.test.datafiles.ImageData> imageList = new ArrayList<>();

    public CoursesData() {

    }

    public CoursesData(int courseID, int position, int rateCount, String name, String descr,
                       String holes, String par, String length, String lengthUnit, String rateUrl,String facilities) {

        this.courseID = courseID;
        this.facilities = facilities;
        this.position = position;
        this.rateCount = rateCount;
        this.name = name;
        this.descr = descr;
        this.holes = holes;
        this.par = par;
        this.length = length;
        this.lengthUnit = lengthUnit;
        this.rateUrl = rateUrl;
    }
}
