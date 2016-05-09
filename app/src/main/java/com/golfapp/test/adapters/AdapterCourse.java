package com.golfapp.test.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.golfapp.test.R;
import com.golfapp.test.datafiles.CoursesData;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Created by Golakiya on 6/29/2015.
 */
public class AdapterCourse extends ArrayAdapter<CoursesData> {


    private Activity context;

    public AdapterCourse(Activity context, List<CoursesData> objects) {
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
                    .into(viewHolder.ivUser, loadImage(viewHolder.ivUser, viewHolder.placeHolderImage, object.imageList.get(0).url,R.drawable.a_place_holder_list_view));
        } else {
            viewHolder.ivUser.setImageResource(R.drawable.a_place_holder_list_view);
        }
        viewHolder.tvTitle.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/B.ttf"));
        viewHolder.tvSubtitle.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/I.ttf"));

        return rowView;
    }

    public Callback loadImage(RoundedImageView ivUser, RoundedImageView placeHolderImage, String url, int placeHolder) {
        return new callback(ivUser, placeHolderImage, url, placeHolder);
    }

    public class callback implements Callback {

        private final int placeHolder;
        RoundedImageView imageView;
        RoundedImageView placeHolderImage;
        String url;

        public callback(RoundedImageView imageView, RoundedImageView placeHolderImage, String url, int placeHolder) {
            this.imageView = imageView;
            this.placeHolderImage = placeHolderImage;
            this.placeHolder = placeHolder;
            this.url = url;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {
            final AtomicBoolean playAnimation = new AtomicBoolean(true);
            Picasso.with(context)
                    .load(url)
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (playAnimation.get()) {
                                placeHolderImage.setVisibility(View.VISIBLE);
                                Animation fadeOut = new AlphaAnimation(0, 1);
                                fadeOut.setInterpolator(new AccelerateInterpolator());
                                fadeOut.setDuration(500);
                                imageView.startAnimation(fadeOut);

                                Animation fadeOutPlaceholder = new AlphaAnimation(1, 0);
                                fadeOutPlaceholder.setInterpolator(new AccelerateInterpolator());
                                fadeOutPlaceholder.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        placeHolderImage.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeOutPlaceholder.setDuration(500);
                                placeHolderImage.startAnimation(fadeOutPlaceholder);
                            }
                            playAnimation.set(false);

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    class ViewHolder {
        RoundedImageView ivUser,placeHolderImage;
        TextView tvTitle, tvSubtitle;
    }
}
