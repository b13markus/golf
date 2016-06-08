package com.golfapp.test.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.golfapp.test.R;
import com.golfapp.test.datafiles.EventsData;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.PDFtools;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;


public class EventDetailActivity extends BaseActivity {

    private String cct = "", cdf = "", plat = "";
    private String on = null, tee = null;
    private int eventID;
    private EventsData selectedEvent;
    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        getSupportActionBar().hide();
        addToBack(this);
        TextView ti = (TextView) findViewById(R.id.title1);
        TextView date = (TextView) findViewById(R.id.sudate);
        TextView sub = (TextView) findViewById(R.id.subtitle);
        TextView rem1 = (TextView) findViewById(R.id.rem1);
        TextView rem2 = (TextView) findViewById(R.id.desc);
        TextView bns = (TextView) findViewById(R.id.bns);

        Typeface tp = Typeface.createFromAsset(getAssets(), "fonts/B.ttf");
        Typeface lt = Typeface.createFromAsset(getAssets(), "fonts/LT.ttf");
        eventID = getIntent().getIntExtra("EventID", 0);
        selectedEvent = Select.from(EventsData.class).where(Condition.prop("event_id").eq(eventID)).first();
        if(selectedEvent!=null) {
            ti.setText(selectedEvent.name);
            sub.setText(selectedEvent.format);
            Linkify.addLinks(ti, Linkify.WEB_URLS);
            ti.setLinkTextColor(Color.parseColor("#94BC0D"));

            Linkify.addLinks(sub, Linkify.WEB_URLS);
            sub.setLinkTextColor(Color.parseColor("#94BC0D"));


            String rf = selectedEvent.remark1;
            if (rf.equals("") || rf.equals("null")) {
                rem1.setVisibility(View.GONE);
            } else {
                rem1.setText(rf);
                Linkify.addLinks(rem1, Linkify.WEB_URLS);
                rem1.setLinkTextColor(Color.parseColor("#94BC0D"));
            }
            String r = selectedEvent.remark2;
            if (r.equals("") || r.equals("null")) {
                rem2.setVisibility(View.GONE);
            } else {
                rem2.setText(r);
                Linkify.addLinks(rem2, Linkify.WEB_URLS);
                rem2.setLinkTextColor(Color.parseColor("#94BC0D"));
            }
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("", "Timzone--" + format.getTimeZone());
        Date convertedDate = new Date();
        try {
            convertedDate = format.parse(selectedEvent.eventDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        outputFormat.setTimeZone(TimeZone.getDefault());
        date.setText(outputFormat.format(convertedDate));
        RelativeLayout tell = (RelativeLayout) findViewById(R.id.custtel);
        RelativeLayout custem = (RelativeLayout) findViewById(R.id.custemail);
        RelativeLayout pdf = (RelativeLayout) findViewById(R.id.custpdf);
        RelativeLayout custee = (RelativeLayout) findViewById(R.id.custtee);

        try {
            JSONObject ff = new JSONObject(store.getString(Constants.PROFILE_STRING));
            JSONObject adv = ff.getJSONObject("profile");
            cct = adv.getString("phone");
            cdf = adv.getString("email");
            plat = adv.getString("address");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (cct.equals("") || cct.equals("null")) {
            tell.setBackgroundResource(R.drawable.btn_disable_str);
        }
        if (cdf.equals("") || cdf.equals("null")) {
            custem.setBackgroundResource(R.drawable.btn_disable_str);
        }

        findViewById(R.id.custtel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cct.equals("") || cct.equals("null")) {
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    try {
                        intent.setData(Uri.parse("tel:" + cct));
                    } catch (Exception e) {

                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            }
        });

        findViewById(R.id.custaddto).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long timeInMilliseconds = 0;
                String date = selectedEvent.eventDate;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date mDate = null;
                try {
                    mDate = sdf.parse(date);
                    timeInMilliseconds = mDate.getTime();
                    System.out.println("Date in milli :: " + timeInMilliseconds);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar cal = Calendar.getInstance();
                cal.setTime(mDate);
                cal.set(Calendar.HOUR_OF_DAY, 8);
                cal.set(Calendar.MINUTE, 00);
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setType("vnd.android.cursor.item/event");
                intent.putExtra("beginTime", cal.getTimeInMillis());
                intent.putExtra("allDay", false);
                // intent.putExtra("rrule", "FREQ=YEARLY");
                cal.add(Calendar.HOUR_OF_DAY, 8);
                intent.putExtra("endTime", cal.getTimeInMillis());
                intent.putExtra("title", selectedEvent.name);
                String body = getString(R.string.app_name) + "\n\n";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Log.d("", "Timzone--" + format.getTimeZone());
                Date convertedDate = new Date();
                try {
                    convertedDate = format.parse(selectedEvent.eventDate);
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                outputFormat.setTimeZone(TimeZone.getDefault());
                body += outputFormat.format(convertedDate) + "\n";
                body += selectedEvent.name + "\n";
                body += selectedEvent.format + "\n";
                body += selectedEvent.remark1 + "\n";
                body += selectedEvent.remark2 + "\n";
                body.replaceAll("null", "");
                intent.putExtra("description", body);
                intent.putExtra(CalendarContract.Events.EVENT_LOCATION, plat);
                intent.putExtra(CalendarContract.Events.HAS_ALARM, 1);
                startActivity(intent);

            }
        });

        findViewById(R.id.custemail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = "";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Log.d("", "Timzone--" + format.getTimeZone());
                Date convertedDate = new Date();
                try {
                    convertedDate = format.parse(selectedEvent.eventDate);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                outputFormat.setTimeZone(TimeZone.getDefault());

                body += outputFormat.format(convertedDate) + "\n";
                body += selectedEvent.name + "\n";
                body += selectedEvent.format + "\n";
                body += selectedEvent.remark1 + "\n";
                body += selectedEvent.remark2 + "\n";
                if (cdf.equals("") || cdf.equals("null")) {
                } else {
                    Intent emailIntent = null;
                    emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", cdf, null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, selectedEvent.name);
                    emailIntent.putExtra(Intent.EXTRA_TEXT, body);
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            }
        });

        findViewById(R.id.custshare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String body = "";
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Log.d("", "Timzone--" + format.getTimeZone());
                Date convertedDate = new Date();
                try {
                    convertedDate = format.parse(selectedEvent.eventDate);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                outputFormat.setTimeZone(TimeZone.getDefault());

                body += outputFormat.format(convertedDate) + "\n";
                body += selectedEvent.name + "\n";
                body += selectedEvent.format + "\n";
                body += selectedEvent.remark1 + "\n";
                body += selectedEvent.remark2 + "\n";
                appShare(EventDetailActivity.this, body);
            }
        });

        try {
            try {
                on = selectedEvent.fileDetail;
                Log.d("", "File detail--" + on);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d("", "file detail -" + on);
            if (on != null && !on.equals("") && !on.equals("null")) {
                pdf.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (on != null) {
                                PDFtools df = new PDFtools();
                                df.showPDFUrl(EventDetailActivity.this, on);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                Log.d("", "file detail else -");
                pdf.setBackgroundResource(R.drawable.btn_disable_str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            try {
                tee = selectedEvent.fileTeeTime;
            } catch (Exception ee) {
            }
            if (tee != null && !tee.equals("") && !tee.equals("null")) {
                custee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (tee != null) {
                                PDFtools df = new PDFtools();
                                df.showPDFUrl(EventDetailActivity.this, tee);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                custee.setBackgroundResource(R.drawable.btn_disable_str);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        ti.setTypeface(tp);
        date.setTypeface(lt);
        sub.setTypeface(lt);
        bns.setTypeface(tp);
        rem1.setTypeface(lt);
        rem2.setTypeface(lt);

        ((TextView) findViewById(R.id.tvCDNewsFb4)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.tvCDNewsFb1)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.tvCDNewsFb2)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.tvCDNewsFb3)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.tvCDNewsFb5)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.tvCDtee)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));

        setupActionbar();
    }

    private void setupActionbar() {
        try {
            String titt = selectedEvent.name;
            ((TextView) findViewById(R.id.eventActionTitle)).setText(R.string.evt_detail_page_nav_bar);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.eventActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.eventActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.eventActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
