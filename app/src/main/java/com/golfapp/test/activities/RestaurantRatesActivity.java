package com.golfapp.test.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.R;
import com.golfapp.test.adapters.AdapterRestaurantRates;
import com.golfapp.test.datafiles.RestaurantData;
import com.golfapp.test.datafiles.RestaurantRatesData;
import com.golfapp.test.utils.Constants;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Golakiya on 6/29/2015.
 */
public class RestaurantRatesActivity extends BaseActivity {

    private static final int MY_SOCKET_TIMEOUT_MS = 100 * 30;
    // Creating JSON Parser object
    private ListView lv;
    private int restaurantID;
    private RestaurantData selectedRestaurant;
    private List<RestaurantRatesData> rateList = new ArrayList<>();
    private AdapterRestaurantRates adp;
    private RestaurantRatesData selectedRate;
    private String notificationString;
    String url = "";
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        if (store.getString(Constants.PACKAGE + restaurantID) != null) {
            notificationString = store.getString(Constants.PACKAGE + restaurantID);
            store.setString(Constants.PACKAGE + restaurantID, null);
            clearNotification(restaurantID, 0);
            int totalNewsBadgeCount = store.getInt(Constants.RESTAURANTS_PUSH_COUNT, 0);
            store.setInt(Constants.RESTAURANTS_PUSH_COUNT, totalNewsBadgeCount - store.getInt(restaurantID + "", 0));
            store.setInt(restaurantID + "", 0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pros_detail_rates_offer);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);
        addToBack(this);
        restaurantID = getIntent().getIntExtra("RestaurantID", 0);
        selectedRestaurant = Select.from(RestaurantData.class).where(Condition.prop("restaurant_id").eq(restaurantID)).first();
        final Button btn = (Button) findViewById(R.id.share);
        btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        btn.setText(getString(R.string.re_share_btn));
        lv = (ListView) findViewById(R.id.lvProsRateOffer);
        if (selectedRestaurant != null) {
            url = selectedRestaurant.packageUrl;
            loadOflineData();
            if (isNetworkAvailable()) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                getNews();
            } else {
                toast(getString(R.string.no_inet));
            }
        } else {
            url = Constants.urlRestaurantSugg + "?client=" + Constants.clientId + "&language=" + Constants.getLanguage() + "&restaurant=" + restaurantID;
            loadOflineData();
            if (isNetworkAvailable()) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                getNews();
            } else {
                toast(getString(R.string.no_inet));
            }
        }
        setupActionbar();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final int rt = R.drawable.pressed;
        final int bt = R.drawable.btn_round;

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adp.setsel(position);
                selectedRate = rateList.get(position);
                btn.setBackgroundResource(bt);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedRate != null) {
                    String body = "";
                    if (selectedRestaurant != null)
                        body += selectedRate.name + ", ";
                    else
                        body += getIntent().getStringExtra("Name") + ", ";
                    body += selectedRate.name + "\n";
                    body += selectedRate.subTitle + "\n";
                    body += selectedRate.desc + "\n";
                    appShare(RestaurantRatesActivity.this, body);
                }

            }
        });
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.prosOfferActionTitle)).setText(getString(R.string.re_suggestion_nav_bar));
        ((TextView) findViewById(R.id.prosOfferActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.prosOfferActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.prosOfferActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    private void loadOflineData() {
        rateList = Select.from(RestaurantRatesData.class).where(Condition.prop("restaurant_id").eq(restaurantID)).list();
        adp = new AdapterRestaurantRates(this, rateList, true, notificationString);
        lv.setAdapter(adp);
    }

    private void getNews() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url + "&sectoken=" + Constants.md5(), null,
                this, this);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        applicationInstance.addToRequestQueue(jsonObjReq, "News");
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadOflineData();
        if (isNetworkAvailable()) {
            getNews();
        } else {
            toast(getString(R.string.no_inet));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    public class SaveData extends AsyncTask<JSONObject, Void, Void> {

        @Override
        protected Void doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            try {
                if (jsonObject.getInt("success") == 1) {
                    rateList.clear();
                    JSONArray array = jsonObject.getJSONArray("suggestions");
                    for (int a = 0; a < array.length(); a++) {
                        JSONObject obj = array.getJSONObject(a);
                        int rateID = obj.getInt("id");
                        String name = obj.getString("name");
                        String pubdate = obj.getString("pubdate");
                        String descr = obj.getString("descr");
                        String subtitle = obj.getString("subtitle");
                        RestaurantRatesData rate = Select.from(RestaurantRatesData.class).where(Condition.prop("restaurant_id").eq(restaurantID)
                                , Condition.prop("rate_id").eq(rateID)).first();
                        if (rate == null) {
                            rate = new RestaurantRatesData(restaurantID, rateID, name, descr, subtitle, pubdate);
                            rate.save();
                        } else {
                            rate.subTitle = subtitle;
                            rate.desc = descr;
                            rate.name = name;
                            rate.pubDate = pubdate;
                            rate.save();
                        }
                        rateList.add(rate);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
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
                    adp = new AdapterRestaurantRates(RestaurantRatesActivity.this, rateList, true, notificationString);
                    lv.setAdapter(adp);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        super.onResponse(jsonObject);
        new SaveData().execute(jsonObject);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        super.onErrorResponse(volleyError);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("Push", false)) {
            startActivity(new Intent(this, RestaurantActivity.class).putExtra("Push", true));
            finish();
        } else {
            finish();
        }
    }

}
