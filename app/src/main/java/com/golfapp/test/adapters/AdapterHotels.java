package com.golfapp.test.adapters;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.golfapp.test.utils.TinyDB;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import com.golfapp.test.R;
import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.datafiles.HotelData;
import com.golfapp.test.utils.BadgeView;


/**
 * Created by Golakiya on 6/29/2015.
 */
public class AdapterHotels extends ArrayAdapter<HotelData> {

    private BaseActivity context;

    public AdapterHotels(BaseActivity context, List<HotelData> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = context.inflater.inflate(R.layout.lv_pros_image, null);
            viewHolder = new ViewHolder();
            viewHolder.ivUser = (RoundedImageView) convertView.findViewById(R.id.ivListPros);
            viewHolder.placeHolderImage = (RoundedImageView) convertView.findViewById(R.id.placeHolderImage);
            viewHolder.tvInfo = (TextView) convertView.findViewById(R.id.tvInfo);
            viewHolder.notificationBadgeCountTV = (TextView) convertView.findViewById(R.id.notificationBadgeCountTV);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    context.getResources().getDimensionPixelSize(R.dimen.badge_size_list),
                    context.getResources().getDimensionPixelSize(R.dimen.badge_size_list), Gravity.RIGHT);
            viewHolder.notificationBadgeCountTV.setLayoutParams(params);
            viewHolder.bnews = new BadgeView(context);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.data = getItem(position);
        int notificationBadgeCount = context.store.getInt(viewHolder.data.hotelID + "", 0);
        boolean isHotel = TinyDB.getInstance(context).getBoolean(viewHolder.data.hotelID + "", false);
        if (notificationBadgeCount > 0 && isHotel) {
            viewHolder.notificationBadgeCountTV.setText(notificationBadgeCount + "");
            viewHolder.notificationBadgeCountTV.setVisibility(View.VISIBLE);
        } else {
            viewHolder.notificationBadgeCountTV.setVisibility(View.GONE);
        }
        viewHolder.bnews.setTargetView(viewHolder.ivUser);
        viewHolder.bnews.setTextSize(context.getResources().getDimension(R.dimen.badge_textsize));
        viewHolder.tvInfo.setText(viewHolder.data.name);
        if (viewHolder.data.imageList.size() > 0) {
            Picasso.with(context)
                    .load(viewHolder.data.imageList.get(0).url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.a_place_holder_list_view)
                    .error(R.drawable.a_place_holder_list_view)
                    .into(viewHolder.ivUser, context.loadImage(viewHolder.ivUser, viewHolder.placeHolderImage, viewHolder.data.imageList.get(0).url, R.drawable.a_place_holder_list_view));
        } else {
            viewHolder.ivUser.setImageResource(R.drawable.a_place_holder_list_view);
        }
        viewHolder.tvInfo.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf"));
        return convertView;
    }

    class ViewHolder {
        RoundedImageView ivUser, placeHolderImage;
        TextView tvInfo, notificationBadgeCountTV;
        BadgeView bnews;
        HotelData data;
    }
}
