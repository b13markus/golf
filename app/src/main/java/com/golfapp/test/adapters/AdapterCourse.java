package com.golfapp.test.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.golfapp.test.R;
import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.datafiles.CoursesData;


/**
 * Created by Golakiya on 6/29/2015.
 */
public class AdapterCourse extends ArrayAdapter<CoursesData> {


    private BaseActivity context;

    public AdapterCourse(BaseActivity context, List<CoursesData> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.lv_course, null);
            viewHolder = new ViewHolder();
            viewHolder.placeHolderImage = (RoundedImageView) rowView.findViewById(R.id.placeHolderImage);
            viewHolder.ivUser = (RoundedImageView) rowView.findViewById(R.id.ivListCourse);
            viewHolder.tvTitle = (TextView) rowView.findViewById(R.id.tvInfoCourseTitle);
            viewHolder.tvSubtitle = (TextView) rowView.findViewById(R.id.tvInfoCourseStitle);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        CoursesData object = getItem(position);
        viewHolder.tvTitle.setText(object.name);
        String hole = object.holes + "" + context.getResources().getString(R.string.holes);
        String par = "Par " + object.par;
        String unit = object.lengthUnit;
        String len = "";
        if (unit.equals("meter")) {
            len = object.length + " m";
        } else {
            len = object.length + " yd";
        }
        viewHolder.tvSubtitle.setText(hole + " - " + par + " - " + len);

        if (object.imageList.size() > 0) {
            Picasso.with(context)
                    .load(object.imageList.get(0).url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(R.drawable.a_place_holder_list_view)
                    .error(R.drawable.a_place_holder_list_view)
                    .into(viewHolder.ivUser, context.loadImage(viewHolder.ivUser, viewHolder.placeHolderImage, object.imageList.get(0).url,R.drawable.a_place_holder_list_view));
        } else {
            viewHolder.ivUser.setImageResource(R.drawable.a_place_holder_list_view);
        }
        viewHolder.tvTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf"));
        viewHolder.tvSubtitle.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/I.ttf"));

        return rowView;
    }


    class ViewHolder {
        RoundedImageView ivUser,placeHolderImage;
        TextView tvTitle, tvSubtitle;
    }
}
