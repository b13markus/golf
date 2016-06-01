package com.golfapp.test.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.golfapp.test.R;
import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.activities.EventDetailActivity;
import com.golfapp.test.datafiles.EventsData;
import com.golfapp.test.utils.PDFtools;

/**
 * Created by Golakiya on 7/29/2015.
 */
public class AdapterEvent extends ArrayAdapter<EventsData> {

    private BaseActivity context;
    private int event = 1;
    private PDFtools df;
    private boolean mShowUrl;

    //1 Upcoming , //0 Past


    public AdapterEvent(BaseActivity context, List<EventsData> objects, boolean showUrl) {
        super(context, 0, objects);
        this.context = context;
        mShowUrl = showUrl;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_event, null);
            viewHolder = new ViewHolder();
            viewHolder.tvEventDay = (TextView) convertView.findViewById(R.id.dateet);
            viewHolder.tvDesc1 = (TextView) convertView.findViewById(R.id.lvEventDesc1);
            viewHolder.tvDesc2 = (TextView) convertView.findViewById(R.id.lvEventDesc2);


            viewHolder.ivEventWinner = (ImageView) convertView.findViewById(R.id.ivEventWinner);
            viewHolder.tvDesc3 = (TextView) convertView.findViewById(R.id.lvEventDesc3);
            viewHolder.tvDesc4 = (TextView) convertView.findViewById(R.id.lvEventDesc4);
            viewHolder.arrow = (ImageView) convertView.findViewById(R.id.ivEventArrow);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvDesc1.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf"));
        viewHolder.tvDesc2.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LT.ttf"));
        if (mShowUrl) {
            Linkify.addLinks(viewHolder.tvDesc1, Linkify.WEB_URLS);
            viewHolder.tvDesc1.setLinkTextColor(Color.parseColor("#94BC0D"));
        }

        viewHolder.tvDesc3.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LT.ttf"));
        viewHolder.tvDesc4.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LT.ttf"));
        viewHolder.tvEventDay.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf"));
        viewHolder.obj = getItem(position);
        if (viewHolder.obj.isUpcomingEvent == 0) {
            viewHolder.arrow.setVisibility(View.GONE);
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            /*if (!viewHolder.obj.timeZone.equals("")) {
                format.setTimeZone(TimeZone.getTimeZone(viewHolder.obj.timeZone));
            }*/
            Date convertedDate = new Date();
            try {
                convertedDate = format.parse(viewHolder.obj.eventDate);
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE dd-MM-yyyy");
            outputFormat.setTimeZone(TimeZone.getDefault());
            String dtt = outputFormat.format(convertedDate);
            String upperString = dtt.substring(0, 1).toUpperCase() + dtt.substring(1);
            viewHolder.tvEventDay.setText(upperString);

            String nam = viewHolder.obj.name.toString();
            if (nam.equals("") || nam.equals("null")) {
            } else {
                viewHolder.tvDesc1.setText(nam);
            }
            String formats = "", mem = "";
            formats = viewHolder.obj.format.toString();

            if (!format.equals("") && !format.equals("null")) {
                viewHolder.tvDesc2.setText(formats);
            } else {

            }
            mem = viewHolder.obj.remark1.toString();
            if (!mem.equals("") && !mem.equals("null")) {
                viewHolder.tvDesc3.setText(mem);
                viewHolder.tvDesc4.setText(viewHolder.obj.remark2.toString());
            } else {
                viewHolder.tvDesc3.setText(viewHolder.obj.remark2.toString());
                viewHolder.tvDesc4.setText("");
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
        if (viewHolder.obj.isUpcomingEvent == 0 && viewHolder.obj.fileResult != null) {
            if (!viewHolder.obj.fileResult.equals("") && !viewHolder.obj.fileResult.equals("null")) {
                String exts = "";
                if (viewHolder.obj.fileResult.contains(".")) {
                    exts = viewHolder.obj.fileResult.substring(viewHolder.obj.fileResult.lastIndexOf("."));
                }
                viewHolder.ivEventWinner.setVisibility(View.VISIBLE);
                viewHolder.arrow.setVisibility(View.GONE);
                viewHolder.ivEventWinner.setPadding(10, 10, 10, 10);
                viewHolder.ivEventWinner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        df.showPDFUrl(context, viewHolder.obj.fileResult);
                    }
                });
            } else {
                viewHolder.ivEventWinner.setVisibility(View.GONE);
            }
        } else {
            viewHolder.ivEventWinner.setVisibility(View.GONE);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("", "on covertview click " + event);
                ViewHolder holder = (ViewHolder) v.getTag();
                if (holder.obj.isUpcomingEvent == 1) {
                    Intent it = new Intent(context, EventDetailActivity.class);
                    it.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    it.putExtra("EventID", holder.obj.eventID);
                    context.startActivity(it);
                }
            }
        });
        if (mShowUrl) {
            Linkify.addLinks(viewHolder.tvDesc2, Linkify.WEB_URLS);
            viewHolder.tvDesc2.setLinkTextColor(Color.parseColor("#94BC0D"));
        }

        return convertView;
    }

    class ViewHolder {
        EventsData obj;
        TextView tvEventMon, tvEventDay, tvEventDate, tvDesc1, tvDesc2, tvDesc3, tvDesc4;
        ImageView arrow, ivEventWinner;
    }
}
