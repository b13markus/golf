package com.golfapp.test.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.golfapp.test.R;
import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.datafiles.RestaurantRatesData;

/**
 * Created by Golakiya on 6/29/2015.
 */
public class AdapterRestaurantRates extends ArrayAdapter<RestaurantRatesData> {

    private final BaseActivity context;
    private final String notificationString;
    private boolean ishotel = false;
    private int sel = -1;

    public AdapterRestaurantRates(BaseActivity context, List<RestaurantRatesData> objects, boolean ishotel, String notificationString) {
        super(context, 0, objects);
        this.context = context;
        this.notificationString = notificationString;
        this.ishotel = ishotel;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        com.golfapp.test.datafiles.RestaurantRatesData obj = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lv_pros_text, null);
            viewHolder = new ViewHolder();
            viewHolder.tit = (TextView) convertView.findViewById(R.id.title1);
            viewHolder.stit = (TextView) convertView.findViewById(R.id.subtitle);
            viewHolder.badge = (ImageView) convertView.findViewById(R.id.badge);
            viewHolder.ll = (LinearLayout) convertView.findViewById(R.id.linee);
            viewHolder.jst = (com.golfapp.test.utils.JustifiedTextView) convertView.findViewById(R.id.jst);
            viewHolder.jst1 = (TextView) convertView.findViewById(R.id.jst1);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //  TextView desc = (TextView) rowView.findViewById(R.id.descc);
        //DocumentView dv=(DocumentView)rowView.findViewById(R.id.desctextpros); rowView.setOnClickListener(new View.OnClickListener() {
        if (notificationString != null) {
            if (notificationString.contains(obj.rateID + "")) {
                viewHolder.badge.setVisibility(View.VISIBLE);
            } else {
                viewHolder.badge.setVisibility(View.GONE);
            }
        }
        if (position == sel) {
            viewHolder.ll.setBackgroundResource(R.drawable.pressed);
            viewHolder.tit.setTextColor(Color.WHITE);
        } else {
            viewHolder.ll.setBackgroundResource(R.drawable.tvgg);
            viewHolder.tit.setTextColor(context.getResources().getColor(R.color.actionBarColor));
        }
        viewHolder.jst1.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/LT.ttf"));
        viewHolder.tit.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf"));
        viewHolder.stit.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/I.ttf"));
        viewHolder.jst.setTypeFace(Typeface.createFromAsset(context.getAssets(), "fonts/LT.ttf"));
        viewHolder.tit.setText(obj.name);
        viewHolder.stit.setText(obj.subTitle);
        viewHolder.jst.setText(obj.desc);


        Linkify.addLinks(viewHolder.stit, Linkify.WEB_URLS);
        viewHolder.stit.setLinkTextColor(Color.parseColor("#94BC0D"));


        viewHolder.jst1.setText(obj.desc);


        Linkify.addLinks(viewHolder.jst1, Linkify.WEB_URLS);
        viewHolder.jst1.setLinkTextColor(Color.parseColor("#94BC0D"));
        return convertView;
    }

    public void setsel(int po) {
        sel = po;
        notifyDataSetChanged();
    }

    class ViewHolder {
        ImageView badge;
        TextView tit;
        TextView stit;
        LinearLayout ll;
        com.golfapp.test.utils.JustifiedTextView jst;
        TextView jst1;
    }

}
