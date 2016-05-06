package com.golfapp.test.datafiles;

import com.orm.SugarRecord;

/**
 * Created by aman on 11/26/2015.
 */
public class ProShopRatesData extends SugarRecord<ProShopRatesData> {
    public int proShopID;
    public int rateID;
    public String name;
    public String desc;
    public String subTitle;
    public String pubDate;

    public ProShopRatesData() {

    }

    public ProShopRatesData(int proShopID, int rateID, String name, String desc, String subTitle, String pubDate) {
        this.proShopID = proShopID;
        this.rateID = rateID;
        this.name = name;
        this.desc = desc;
        this.subTitle = subTitle;
        this.pubDate = pubDate;

    }


}
