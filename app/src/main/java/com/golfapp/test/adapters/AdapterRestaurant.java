package com.golfapp.test.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.golfapp.test.GcmConstants;
import com.golfapp.test.R;
import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.datafiles.RestaurantData;
import com.golfapp.test.utils.BadgeView;
import com.golfapp.test.utils.TinyDB;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Golakiya on 6/29/2015.
 */
public class AdapterRestaurant extends ArrayAdapter<RestaurantData> {


    private BaseActivity context;

    public AdapterRestaurant(BaseActivity context, List<RestaurantData> objects) {
        super(context, 0, objects);
        this.context = context;
    }

    @Override
    public View getView(int position, View rowView, ViewGroup parent) {

        ViewHolder viewHolder;
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.lv_pros_image, null);
            viewHolder = new ViewHolder();
            viewHolder.ivUser = (RoundedImageView) rowView.findViewById(R.id.ivListPros);
            viewHolder.placeHolderImage = (RoundedImageView) rowView.findViewById(R.id.placeHolderImage);
            viewHolder.tvInfo = (TextView) rowView.findViewById(R.id.tvInfo);
            viewHolder.notificationBadgeCountTV = (TextView) rowView.findViewById(R.id.notificationBadgeCountTV);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    context.getResources().getDimensionPixelSize(R.dimen.badge_size_list),
                    context.getResources().getDimensionPixelSize(R.dimen.badge_size_list), Gravity.RIGHT);
            viewHolder.notificationBadgeCountTV.setLayoutParams(params);
            rowView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }
        RestaurantData data = getItem(position);
        int notificationBadgeCount = context.store.getInt(GcmConstants.RESTAURANT + data.restaurantID + "", 0);
        boolean isRestaurant = TinyDB.getInstance(context).getBoolean(GcmConstants.RESTAURANT + data.restaurantID + "", false);
        if (notificationBadgeCount > 0 && isRestaurant) {
            viewHolder.notificationBadgeCountTV.setText(notificationBadgeCount + "");
            viewHolder.notificationBadgeCountTV.setVisibility(View.VISIBLE);
        } else {
            viewHolder.notificationBadgeCountTV.setVisibility(View.GONE);
        }
        if (data.imageList.size() > 0) {
            Picasso.with(context)
                    .load(data.imageList.get(0).url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.a_place_holder_list_view)
                    .error(R.drawable.a_place_holder_list_view)
                    .into(viewHolder.ivUser, context.loadImage(viewHolder.ivUser, viewHolder.placeHolderImage, data.imageList.get(0).url,R.drawable.a_place_holder_list_view));
        } else {
            viewHolder.ivUser.setImageResource(R.drawable.a_place_holder_list_view);
        }
        viewHolder.tvInfo.setText(data.name);
        viewHolder.tvInfo.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf"));
        return rowView;
    }

    class ViewHolder {
        RoundedImageView ivUser,placeHolderImage;
        TextView tvInfo, notificationBadgeCountTV;
        BadgeView bnews;
    }
}
