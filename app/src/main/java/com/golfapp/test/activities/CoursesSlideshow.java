package com.golfapp.test.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.golfapp.test.AppConstants;
import com.golfapp.test.R;
import com.golfapp.test.fragments.ImageFragment;
import com.golfapp.test.utils.CirclePageIndicator;

/**
 * Created by Golakiya on 7/5/2015.
 */
public class CoursesSlideshow extends BaseActivity {

    private int[] coursesRes = new int[0] ;

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

    private void initResImages() {
        if(AppConstants.currentCourse == 0){
            coursesRes = AppConstants.course_1;
        }else if(AppConstants.currentCourse == 1){
            coursesRes = AppConstants.course_2;
        }else if(AppConstants.currentCourse == 2){
            coursesRes = AppConstants.course_3;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_image);
        getSupportActionBar().hide();
        initResImages();
        String tit = getIntent().getStringExtra("title");
        setupActionbar();
        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        ImageAdapter adapter = new ImageAdapter(getFragmentManager());
        pager.setAdapter(adapter);
        mIndicator.setViewPager(pager);
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.courseDeatilActionTitle)).setText(getString(R.string.crs_the_course_btn_detail_nav_bar));
        ((TextView) findViewById(R.id.courseDeatilActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.courseDeatilActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.courseDeatilActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }


    private class ImageAdapter extends FragmentStatePagerAdapter {
        public ImageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            ImageFragment fragment = new ImageFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("Image", coursesRes[position]);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return coursesRes.length;
        }
    }
}
