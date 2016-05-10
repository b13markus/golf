package com.golfapp.test.adapters;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.golfapp.test.R;
import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.datafiles.ProsData;
import com.golfapp.test.utils.BadgeView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * Created by Golakiya on 6/29/2015.
 */
public class AdapterPros extends ArrayAdapter<ProsData> {

    private BaseActivity context;

    public AdapterPros(BaseActivity context, List<ProsData> objects) {
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
        int notificationBadgeCount = context.store.getInt(viewHolder.data.prosID + "", 0);
        if (notificationBadgeCount > 0) {
            viewHolder.notificationBadgeCountTV.setText(notificationBadgeCount + "");
            viewHolder.notificationBadgeCountTV.setVisibility(View.VISIBLE);
        } else {
            viewHolder.notificationBadgeCountTV.setVisibility(View.GONE);
        }
        viewHolder.bnews.setTargetView(viewHolder.ivUser);
        viewHolder.bnews.setTextSize(context.getResources().getDimension(R.dimen.badge_textsize));
        viewHolder.tvInfo.setText(viewHolder.data.name);
        /*if (viewHolder.data.imageList.size() > 0) {
            if (!viewHolder.data.imageList.get(0).isLoaded)
                context.il.loadImage(viewHolder.data.imageList.get(0).url, context.options, new Listener(viewHolder));
            else
                context.il.displayImage(viewHolder.data.imageList.get(0).url, viewHolder.ivUser);
        } else {
            viewHolder.ivUser.setImageResource(R.drawable.place_holder);
        }*/
        if (viewHolder.data.imageList.size() > 0) {
            Picasso.with(context)
                    .load(viewHolder.data.imageList.get(0).url)
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(R.drawable.a_place_holder_list_view)
                    .error(R.drawable.a_place_holder_list_view)
                    .into(viewHolder.ivUser, context.loadImage(viewHolder.ivUser, viewHolder.placeHolderImage, viewHolder.data.imageList.get(0).url,R.drawable.a_place_holder_list_view));
        } else {
            viewHolder.ivUser.setImageResource(R.drawable.a_place_holder_list_view);
        }
        viewHolder.tvInfo.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf"));
        return convertView;
    }

    class ViewHolder {
        RoundedImageView ivUser,placeHolderImage;
        TextView tvInfo, notificationBadgeCountTV;
        BadgeView bnews;
        ProsData data;
    }
}
