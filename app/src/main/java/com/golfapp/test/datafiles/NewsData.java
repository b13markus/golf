package com.golfapp.test.datafiles;

import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by aman on 11/23/2015.
 */
public class NewsData extends SugarRecord {
    public int newsID;

    public String title;
    public String subtitle;
    public String desc;
    public String pubDate;
    public String updated;

    @Ignore
    public List<ImageData> imageList = new ArrayList<>();

    public NewsData() {

    }

    public NewsData(int newsID, String title, String subtitle, String desc, String pubDate, String updated) {
        this.newsID = newsID;
        this.title = title;
        this.subtitle = subtitle;
        this.desc = desc;
        this.pubDate = pubDate;
        this.updated = updated;

    }
}
