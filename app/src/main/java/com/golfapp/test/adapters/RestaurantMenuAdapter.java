package com.golfapp.test.adapters;

import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.golfapp.test.R;
import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.datafiles.RestaurantMenuData;


/**
 * Created by aman on 11/26/2015.
 */
public class RestaurantMenuAdapter extends ArrayAdapter<RestaurantMenuData> {

    Typeface b, lt;
    BaseActivity context;


    public RestaurantMenuAdapter(BaseActivity context, List<RestaurantMenuData> objects) {
        super(context, 0, objects);
        this.context = context;
        b = Typeface.createFromAsset(context.getAssets(), "fonts/BI.ttf");
        lt = Typeface.createFromAsset(context.getAssets(), "fonts/LT.ttf");
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RestaurantMenuData data = getItem(position);
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = context.inflater.inflate(R.layout.course_item_view, null);
            holder.decTV = (TextView) convertView.findViewById(R.id.descTV);
            holder.sectionTV = (TextView) convertView.findViewById(R.id.sectionTV);
            holder.rate = (TextView) convertView.findViewById(R.id.price);
            holder.sectionRl = (RelativeLayout) convertView.findViewById(R.id.sectionRL);
            holder.itemRL = (RelativeLayout) convertView.findViewById(R.id.itemRL);

            holder.descBottomTV = (TextView) convertView.findViewById(R.id.descBottomTV);
            holder.sectionTopTV = (TextView) convertView.findViewById(R.id.sectionTopTV);
            holder.priceBottom = (TextView) convertView.findViewById(R.id.priceBottom);
            holder.sectionTopRL = (RelativeLayout) convertView.findViewById(R.id.sectionTopRL);
            holder.itemBottomRL = (RelativeLayout) convertView.findViewById(R.id.itemBottomRL);
            holder.decTV.setTypeface(lt);
            holder.rate.setTypeface(lt);
            holder.sectionTV.setTypeface(b);


            holder.descBottomTV.setTypeface(lt);
            holder.sectionTopTV.setTypeface(b);
            holder.priceBottom.setTypeface(lt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (data.isSection == 0) {
            if (position == (getCount() - 1)) {
                holder.itemRL.setVisibility(View.GONE);
                holder.itemBottomRL.setVisibility(View.VISIBLE);
                holder.sectionTopRL.setVisibility(View.GONE);
                holder.sectionRl.setVisibility(View.GONE);
                holder.descBottomTV.setText(data.descr);
                holder.priceBottom.setText(data.price);
            } else {
                holder.itemRL.setVisibility(View.VISIBLE);
                holder.itemBottomRL.setVisibility(View.GONE);
                holder.sectionTopRL.setVisibility(View.GONE);
                holder.sectionRl.setVisibility(View.GONE);
                holder.decTV.setText(data.descr);
                holder.rate.setText(data.price);
            }

        } else {
            if (position == 0) {
                holder.itemRL.setVisibility(View.GONE);
                holder.itemBottomRL.setVisibility(View.GONE);
                holder.sectionTopRL.setVisibility(View.VISIBLE);
                holder.sectionRl.setVisibility(View.GONE);
                holder.sectionTopTV.setText(data.section);
            } else {
                holder.itemRL.setVisibility(View.GONE);
                holder.itemBottomRL.setVisibility(View.GONE);
                holder.sectionTopRL.setVisibility(View.GONE);
                holder.sectionRl.setVisibility(View.VISIBLE);
                holder.sectionTV.setText(data.section);
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        public RelativeLayout sectionRl, itemBottomRL, sectionTopRL, itemRL;
        public TextView decTV, descBottomTV, sectionTV, sectionTopTV, priceBottom, rate;
    }

}

