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
import com.golfapp.test.R;
import com.golfapp.test.adapters.RestaurantMenuAdapter;
import com.golfapp.test.datafiles.RestaurantData;
import com.golfapp.test.datafiles.RestaurantMenuData;
import com.golfapp.test.utils.Constants;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class RestaurantMenu extends BaseActivity {

    private ListView lv;
    private int restaurantID;
    private com.golfapp.test.datafiles.RestaurantData selectedCourse;
    private boolean clearList;
    private int total;
    private List<RestaurantMenuData> list = new ArrayList<>();
    private RestaurantMenuAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
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
        getSupportActionBar().hide();
        setContentView(R.layout.activity_the_courses);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        clearList = true;
        lv = (ListView) findViewById(R.id.lvc);
        restaurantID = getIntent().getIntExtra("RestaurantID", 0);
        selectedCourse = Select.from(RestaurantData.class).where(Condition.prop("restaurant_id").eq(restaurantID)).first();
        if (!isNetworkAvailable()) {
            loadOfflineData();
        } else {
            swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_green_light);
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
    public void onRefresh() {
        super.onRefresh();
        if (isNetworkAvailable()) {
            getCourseRates();
        } else {
            toast(getString(R.string.no_inet));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void checkIfUpdateReuired(final int prosID) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                selectedCourse.menuUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getInt("success") == 1) {
                                JSONArray prosArray = jsonObject.getJSONArray("menus");
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


    @Override
    public void networkAvailable() {
        super.networkAvailable();
        getCourseRates();
    }

    private void getCourseRates() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                selectedCourse.menuUrl+"&sectoken="+ Constants.md5(), null,
                this, this);
        applicationInstance.addToRequestQueue(jsonObjReq, "Pros");
    }

    private void loadOfflineData() {
        list = Select.from(RestaurantMenuData.class).where(Condition.prop("restaurant_id").eq(restaurantID)).list();
        setListView();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        super.onErrorResponse(volleyError);
        swipeRefreshLayout.setRefreshing(false);
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
                    total = jsonObject.getJSONObject("paging").getInt("total");
                    JSONArray prosArray = jsonObject.getJSONArray("menus");
                    for (int a = 0; a < prosArray.length(); a++) {
                        JSONObject obj = prosArray.getJSONObject(a);
                        int ratesID = obj.getInt("id");
                        String section = obj.getString("section");
                        RestaurantMenuData heading = Select.from(RestaurantMenuData.class).where(Condition.prop("restaurant_id").eq(restaurantID),
                                Condition.prop("rate_id").eq(ratesID)).first();
                        if (heading == null) {
                            heading = new RestaurantMenuData(restaurantID, ratesID, 0, 1, section, "", "");
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
                            RestaurantMenuData item = Select.from(RestaurantMenuData.class).where(Condition.prop("restaurant_id").eq(restaurantID),
                                    Condition.prop("rate_id").eq(ratesID), Condition.prop("item_id").eq(itemID)).first();
                            if (item == null) {
                                item = new RestaurantMenuData(restaurantID, ratesID, itemID, 0, "", descr, price);
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

    private void setListView() {
        if (clearList) {
            clearList = false;
            adapter = new RestaurantMenuAdapter(this, list);
            lv.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.courseDetailActionTitle)).setText(R.string.re_menu_nav_bar);
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
}
