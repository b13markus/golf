package com.golfapp.test.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.golfapp.test.AppConstants;
import com.golfapp.test.R;
import com.golfapp.test.fragments.ImageFragment;
import com.golfapp.test.utils.CirclePageIndicator;
import com.squareup.picasso.Picasso;

import java.util.ArrayDeque;


/**
 * Created by Golakiya on 7/5/2015.
 */
public class CoursesSlideshow extends BaseActivity {

    String tit = "";
    private ViewPager mViewPager;
    TextView imgNameTxt;
    CirclePageIndicator mIndicator;
    private ArrayDeque<ImageView> mRecycledViewsList;
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
        tit = getIntent().getStringExtra("title");
        addToStack(this);
        setupActionbar();
        mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        MyPagerAdapter adapter = new MyPagerAdapter();
        /*ImageAdapter adapter = new ImageAdapter(getSupportFragmentManager());*/
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


    private class ImageAdapter extends FragmentPagerAdapter {


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


    private class MyPagerAdapter extends PagerAdapter {

        LayoutInflater mLayoutInflater;

        public MyPagerAdapter() {
            mRecycledViewsList = new ArrayDeque<>();
            mLayoutInflater = (LayoutInflater) CoursesSlideshow.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        ImageView primaryView;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView view = null;
            if (mRecycledViewsList.isEmpty()) {
                view = new ImageView(CoursesSlideshow.this);
            } else {
                view = mRecycledViewsList.pop();
            }
            view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
                    LayoutParams.WRAP_CONTENT));
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);

            //view.setBackgroundResource(imgs[position]);
            Picasso.with(getApplicationContext()).load(coursesRes[position]).into(view);
            ((ViewPager) container).addView(view, 0);
            return view;
        }

        @Override
        public int getCount() {
            return coursesRes.length;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ViewPager pager = (ViewPager) container;
            ImageView recycledView = (ImageView) object;
            pager.removeView(recycledView);
            mRecycledViewsList.push(recycledView);
        }


        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == ((View) arg1);
        }

        public Parcelable saveState() {
            return null;
        }
    }

}
