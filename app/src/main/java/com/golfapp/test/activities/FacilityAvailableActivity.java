package com.golfapp.test.activities;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;

import com.golfapp.test.R;
import com.golfapp.test.datafiles.CoursesData;

/**
 * Created by Golakiya on 7/5/2015.
 */


public class FacilityAvailableActivity extends BaseActivity {

    int shower = 0, clubhouse = 0, proshop = 0, pro = 0,
            clubhaire = 0, buggy = 0, practice = 0, putting = 0,
            pitching = 0, restaurant = 0, hotel = 0, pool = 0, tennis = 0,
            wifi = 0, parking = 0;
    String tit = "";
    int courseID;
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
        addToStack(this);
        setContentView(R.layout.activity_facility);
        getSupportActionBar().hide();
        courseID = getIntent().getIntExtra("CourseID", 0);
        selectedCourse = Select.from(CoursesData.class).where(Condition.prop("course_id").eq(courseID)).first();
        JSONArray arr = new JSONArray();
        try {
            arr = new JSONArray(selectedCourse.facilities);
            for (int i = 0; i < arr.length(); i++) {
                switch (arr.getString(i)) {
                    case "Shower":
                        shower = 1;
                        break;
                    case "Pros":
                        pro = 1;
                        break;
                    case "Proshop":
                        proshop = 1;
                        break;
                    case "Club house":
                        clubhouse = 1;
                        break;
                    case "Buggy":
                        buggy = 1;
                        break;
                    case "Club Hire":
                        clubhaire = 1;
                        break;
                    case "Practice":
                        practice = 1;
                        break;
                    case "Putting":
                        putting = 1;
                        break;
                    case "Pitching":
                        pitching = 1;
                        break;
                    case "Hotel":
                        hotel = 1;
                        break;
                    case "Restaurant":
                        restaurant = 1;
                        break;
                    case "Wi-fi":
                        wifi = 1;
                        break;
                    case "Pool":
                        pool = 1;
                        break;
                    case "Tennis":
                        tennis = 1;
                        break;
                    case "Parking":
                        parking = 1;
                        break;


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setupActionbar();

        if (shower == 1)
            ((ImageView) findViewById(R.id.ivShower)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_changing_room_on));
        if (clubhouse == 1)
            ((ImageView) findViewById(R.id.ivClubHouse)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_club_house_on));
        if (proshop == 1)
            ((ImageView) findViewById(R.id.ivProshop)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_proshop_on));
        if (pro == 1)
            ((ImageView) findViewById(R.id.ivPros)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_pro_on));
        if (clubhaire == 1)
            ((ImageView) findViewById(R.id.ivClubhair)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_club_hire_on));
        if (buggy == 1)
            ((ImageView) findViewById(R.id.ivBuggy)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_buggy_on));
        if (practice == 1)
            ((ImageView) findViewById(R.id.ivPractice)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_practice_on));
        if (putting == 1)
            ((ImageView) findViewById(R.id.ivPutting)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_putting_on));
        if (pitching == 1)
            ((ImageView) findViewById(R.id.ivPitching)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_pitching_on));
        if (restaurant == 1)
            ((ImageView) findViewById(R.id.ivRestaurent)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_restaurant_on));
        if (hotel == 1)
            ((ImageView) findViewById(R.id.ivHotel)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_hotel_on_site_on));
        if (pool == 1)
            ((ImageView) findViewById(R.id.ivPool)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_poll_on));
        if (tennis == 1)
            ((ImageView) findViewById(R.id.ivTennis)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_tennis_on));
        if (wifi == 1)
            ((ImageView) findViewById(R.id.ivWifi)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_wifi_on));
        if (parking == 1)
            ((ImageView) findViewById(R.id.ivParking)).setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.facilites_parking_on));

        ((TextView) findViewById(R.id.tvShower)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvClubHouse)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvProshop)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvPros)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvClub)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvBuggy)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvPractice)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvPutting)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvPitching)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvRestaurent)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvHotel)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvPool)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvTennis)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvWifi)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));
        ((TextView) findViewById(R.id.tvParking)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/LT.ttf"));


    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.facilityActionTitle)).setText(getString(R.string.crs_facilities_details_nav_bar));
        ((TextView) findViewById(R.id.facilityActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.facilityActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.facilityActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }
}
