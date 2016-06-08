package com.golfapp.test.datafiles;

import com.orm.SugarRecord;

/**
 * Created by aman on 11/24/2015.
 */
public class EventsData extends SugarRecord {

    public int eventID;
    public int isUpcomingEvent;
    public String name;
    public String eventDate;
    public String format;
    public String remark1;
    public String fileDetail;
    public String remark2;
    public String fileTeeTime;
    public String fileResult;
    public String fileDefault;
    public String pubdate;

    public EventsData() {

    }

    public EventsData(int eventID, int isUpcomingEvent, String name, String eventDate, String format,
                      String remark1, String fileDetail, String remark2, String fileTeeTime, String fileResult,
                      String fileDefault, String pubdate) {
        this.eventID = eventID;
        this.isUpcomingEvent = isUpcomingEvent;
        this.eventDate = eventDate;
        this.name = name;
        this.format = format;
        this.remark1 = remark1;
        this.fileDetail = fileDetail;
        this.fileTeeTime = fileTeeTime;
        this.fileResult = fileResult;
        this.remark2 = remark2;
        this.fileDefault = fileDefault;
        this.pubdate = pubdate;

    }
}
