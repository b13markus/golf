package com.golfapp.test.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.adapters.CourseRatesAdapter;
import com.golfapp.test.datafiles.CourseRateData;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.golfapp.test.R;


public class CourseRateoffActivity extends com.golfapp.test.activities.BaseActivity {

    private ListView lv;
    private int courseID;
    private com.golfapp.test.datafiles.CoursesData selectedCourse;
    private boolean clearList;
    private List<CourseRateData> list = new ArrayList<>();
    private CourseRatesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_the_courses);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        getSupportActionBar().hide();
        lv = (ListView) findViewById(R.id.lvc);
        clearList = true;
        courseID = getIntent().getIntExtra("CourseID", 0);
        selectedCourse = Select.from(com.golfapp.test.datafiles.CoursesData.class).where(Condition.prop("course_id").eq(courseID)).first();
        if (!isNetworkAvailable()) {
            loadOfflineData();
        } else {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
        setupActionbar();
    }

    @Override
    public void networkAvailable() {
        super.networkAvailable();
        getCourseRates();
    }

    private void getCourseRates() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                selectedCourse.rateUrl+"&sectoken="+ com.golfapp.test.utils.Constants.md5(), null,
                this, this);
        applicationInstance.addToRequestQueue(jsonObjReq, "Pros");
    }

    private void loadOfflineData() {
        list = Select.from(com.golfapp.test.datafiles.CourseRateData.class).where(Condition.prop("course_id").eq(courseID)).list();
        setListView();
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        super.onResponse(jsonObject);
        new SaveData().execute(jsonObject);
    }

    public class SaveData extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            try {
                if (clearList) {
                    list.clear();
                }
                if (jsonObject.getInt("success") == 1) {
                    int total = jsonObject.getJSONObject("paging").getInt("total");
                    JSONArray prosArray = jsonObject.getJSONArray("rates");
                    for (int a = 0; a < prosArray.length(); a++) {
                        JSONObject obj = prosArray.getJSONObject(a);
                        int ratesID = obj.getInt("id");
                        String section = obj.getString("section");
                        com.golfapp.test.datafiles.CourseRateData heading = Select.from(com.golfapp.test.datafiles.CourseRateData.class).where(Condition.prop("course_id").eq(courseID),
                                Condition.prop("rate_id").eq(ratesID)).first();
                        if (heading == null) {
                            heading = new com.golfapp.test.datafiles.CourseRateData(courseID, ratesID, 0, 1, section, "", "");
                            heading.save();
                        } else {
                            heading.section = section;
                            heading.save();
                        }
                        list.add(heading);
                        JSONArray items = obj.getJSONArray("items");
                        for (int b = 0; b < items.length(); b++) {
                            JSONObject itemObj = items.getJSONObject(b);
                            int itemID = itemObj.getInt("id");
                            String descr = itemObj.getString("descr");
                            String price = itemObj.getString("price");
                            com.golfapp.test.datafiles.CourseRateData item = Select.from(com.golfapp.test.datafiles.CourseRateData.class).where(Condition.prop("course_id").eq(courseID),
                                    Condition.prop("rate_id").eq(ratesID), Condition.prop("item_id").eq(itemID)).first();
                            if (item == null) {
                                item = new com.golfapp.test.datafiles.CourseRateData(courseID, ratesID, itemID, 0, "", descr, price);
                                item.save();
                            } else {
                                item.descr = descr;
                                item.price = price;
                                item.save();
                            }
                            list.add(item);
                        }
                    }
                }
            } catch (Exception e) {

            } finally {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setListView();
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (isNetworkAvailable()) {
            clearList = true;
            getCourseRates();
        } else {
            toast(getString(R.string.no_inet));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void checkIfUpdateReuired(final int prosID) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                selectedCourse.rateUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getInt("success") == 1) {
                                JSONArray prosArray = jsonObject.getJSONArray("rates");
                                if (prosArray.length() > 0) {
                                    if (prosID != prosArray.getJSONObject(0).getInt("id")) {        // top Pros ID does not match with our top id means update is available.
                                        getCourseRates();
                                    } else {
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                } else {
                                    swipeRefreshLayout.setRefreshing(false);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        applicationInstance.addToRequestQueue(jsonObjReq, "Pros");
    }

    private void setListView() {
        if (clearList) {
            clearList = false;
            adapter = new com.golfapp.test.adapters.CourseRatesAdapter(this, list);
            lv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.courseDetailActionTitle)).setText(R.string.crs_rate_details_nav_bar);
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
                startActivity(new Intent(getApplicationContext(), com.golfapp.test.activities.MainActivity.class));
            }
        });
    }
}
