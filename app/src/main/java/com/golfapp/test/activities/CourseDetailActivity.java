package com.golfapp.test.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import com.golfapp.test.R;
import com.golfapp.test.datafiles.CoursesData;
import com.golfapp.test.datafiles.ImageData;
import com.golfapp.test.utils.JustifiedTextView;


public class CourseDetailActivity extends BaseActivity {

    private CoursesData selectedCourse;
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
        setContentView(R.layout.activity_course_detail);
        getSupportActionBar().hide();
        Button thecourse = (Button) findViewById(R.id.thecour);
        thecourse.setText(getString(R.string.crs_the_course_btn));
        Button fac = (Button) findViewById(R.id.faci);
        fac.setText(getString(R.string.crs_facilities_btn));
        Button rates = (Button) findViewById(R.id.ratess);
        rates.setText(getString(R.string.crs_rates_btn));
        TextView tit = (TextView) findViewById(R.id.title1);
        tit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        TextView stit = (TextView) findViewById(R.id.subtitle);
        JustifiedTextView jst = (JustifiedTextView) findViewById(R.id.jst);
        SliderLayout mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        Typeface tp = Typeface.createFromAsset(getAssets(), "fonts/B.ttf");
        Typeface lt = Typeface.createFromAsset(getAssets(), "fonts/LT.ttf");
        stit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/I.ttf"));
        jst.setTypeFace(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        TextView des = (TextView) findViewById(R.id.desctext1);
        des.setTypeface(lt);
        thecourse.setTypeface(tp);
        fac.setTypeface(tp);
        rates.setTypeface(tp);
        int courseID = getIntent().getIntExtra("CourseID", 0);
        selectedCourse = Select.from(CoursesData.class).where(Condition.prop("course_id").eq(courseID)).first();
        selectedCourse.imageList = Select.from(ImageData.class).where(Condition.prop("course_id").eq(courseID)).list();
        try {
            tit.setText(selectedCourse.name);
            String hole = selectedCourse.holes + "" + getResources().getString(R.string.holes);
            String par = "Par " + selectedCourse.par;
            String unit = selectedCourse.lengthUnit;
            String len = "";
            if (unit.equals("meter")) {
                len = selectedCourse.length + " m";
            } else {
                len = selectedCourse.length + " yd";
            }
            stit.setText(hole + " - " + par + " - " + len);
            jst.setText(selectedCourse.descr);
            des.setText(selectedCourse.descr);
            String r = selectedCourse.rateCount + "";
            Log.d("", "rr--" + r);
            if (r.compareToIgnoreCase("0") == 0) {
                rates.setBackgroundResource(R.drawable.btn_disable);
            } else {
                rates.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(CourseDetailActivity.this, CourseRateoffActivity.class);
                        it.putExtra("CourseID", selectedCourse.courseID);
                        startActivity(it);
                    }
                });
            }

        } catch (Exception e) {
        }

        if (selectedCourse.imageList.size() <= 0) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
        } else if (selectedCourse.imageList.size() <= 1) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
            RoundedImageView iv = (RoundedImageView) findViewById(R.id.lvProDetail);
            RoundedImageView placeHolder = (RoundedImageView) findViewById(R.id.placeHolderImage);
            iv.setVisibility(View.VISIBLE);
            try {
                Picasso.with(this)
                        .load(selectedCourse.imageList.get(0).url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.a_place_holder_detail_page)
                        .error(R.drawable.a_place_holder_detail_page)
                        .into(iv, loadImage(iv, placeHolder, selectedCourse.imageList.get(0).url,R.drawable.a_place_holder_detail_page));
                //il.loadImage(selectedRoom.imageList.get(0).url, options, new Listener(iv));
            } catch (Exception e) {
                iv.setImageResource(R.drawable.a_place_holder_detail_page);
            }
        } else {
            for (int i = 0; i < selectedCourse.imageList.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                textSliderView.image(selectedCourse.imageList.get(i).url).setScaleType(BaseSliderView.ScaleType.CenterCrop).setOnSliderClickListener(null);
                textSliderView.bundle(new Bundle());
                //    textSliderView.getBundle().putString("extra", name);
                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.stopAutoCycle();
        }

        thecourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(CourseDetailActivity.this, CoursesSlideshow.class);
                it.putExtra("title", selectedCourse.name);
                startActivity(it);
            }
        });
        fac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(CourseDetailActivity.this, FacilityAvailableActivity.class);
                it.putExtra("CourseID", selectedCourse.courseID);
                startActivity(it);
            }
        });

        setupActionbar();
    }



    private void setupActionbar() {

        ((TextView) findViewById(R.id.courseDetailActionTitle)).setText(getString(R.string.crs_the_course_detail_nav_bar));

        ((TextView) findViewById(R.id.courseDetailActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.courseDetailActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.courseDetailActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
/*

    Adshower ads;

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("", "onStop");
        ads = new Adshower(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("", "on resume");
        if (ads != null) {
            ads.isTimeout(this);
        }
    }

*/

}
