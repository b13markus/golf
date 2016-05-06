package com.golfapp.test.datafiles;

import com.orm.SugarRecord;

/**
 * Created by aman on 11/23/2015.
 */
public class ProRates extends SugarRecord<ProRates> {


    public int prosID;
    public int rateID;
    public String name;
    public String desc;
    public String subTitle;
    public String pubDate;

    public ProRates() {

    }

    public ProRates(int prosID, int rateID, String name, String desc, String subTitle, String pubDate) {
        this.prosID = prosID;
        this.rateID = rateID;
        this.name = name;
        this.desc = desc;
        this.subTitle = subTitle;
        this.pubDate = pubDate;

    }

}
