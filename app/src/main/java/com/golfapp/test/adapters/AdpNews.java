package com.golfapp.test.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.golfapp.test.R;
import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.datafiles.NewsData;

/**
 * Created by Golakiya on 6/29/2015.
 */
public class AdpNews extends ArrayAdapter<NewsData> {

    private final BaseActivity context;

    public AdpNews(BaseActivity context, List<NewsData> objects) {
        super(context, 0, objects);
        this.context = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        NewsData obj = getItem(position);
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.lv_news, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.tit = (TextView) rowView.findViewById(R.id.title1);
            viewHolder.stit = (TextView) rowView.findViewById(R.id.subtitle);
            viewHolder.date = (TextView) rowView.findViewById(R.id.date);
            viewHolder.desc = (TextView) rowView.findViewById(R.id.desc);
            viewHolder.badge = (ImageView) rowView.findViewById(R.id.badge);
            rowView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) rowView.getTag();
        int notificationBadgeCount = context.store.getInt(obj.newsID + "", 0);
        if (notificationBadgeCount > 0) {
            holder.badge.setVisibility(View.VISIBLE);
        } else {
            holder.badge.setVisibility(View.GONE);
        }
        if (obj.title.isEmpty()) {
            holder.tit.setVisibility(View.GONE);
        } else {
            holder.tit.setText(obj.title);
            holder.tit.setVisibility(View.VISIBLE);
        }

        if (obj.subtitle.isEmpty()) {
            holder.stit.setVisibility(View.GONE);
        } else {
            holder.stit.setText(obj.subtitle);
            holder.stit.setVisibility(View.VISIBLE);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = format.parse(obj.pubDate);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        holder.date.setText(outputFormat.format(convertedDate));
        if (obj.desc.isEmpty()) {
            holder.desc.setVisibility(View.GONE);
        } else {
            holder.desc.setText(obj.desc);
            holder.desc.setVisibility(View.VISIBLE);
        }
        Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/LT.ttf");
        Typeface b = Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf");
        Typeface i = Typeface.createFromAsset(context.getAssets(), "fonts/I.ttf");
        holder.tit.setTypeface(b);
        holder.stit.setTypeface(i);
        holder.date.setTypeface(tf);
        holder.desc.setTypeface(tf);

        return rowView;
    }


    class ViewHolder {
        TextView tit;
        TextView stit;
        TextView date;
        TextView desc;
        ImageView badge;
    }
}
