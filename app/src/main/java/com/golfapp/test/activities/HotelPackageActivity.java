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
import com.golfapp.test.adapters.AdapterHotelPackage;
import com.golfapp.test.datafiles.HotelData;
import com.golfapp.test.datafiles.HotelRatesData;
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
public class HotelPackageActivity extends BaseActivity {

    private static final int MY_SOCKET_TIMEOUT_MS = 100 * 30;
    private ListView lv;
    private HotelRatesData selected;
    private AdapterHotelPackage adp;
    private int hotelID;
    private HotelData selectedHotel;
    private List<HotelRatesData> rateList = new ArrayList<>();
    private String notificationString;
    private String url = "";
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
        if (store.getString(Constants.PACKAGE + hotelID) != null) {             // Is any notification for this page.
            notificationString = store.getString(Constants.PACKAGE + hotelID);             // Get all the packages which has new notificationwe will use this string to show new tag on list
            store.setString(Constants.PACKAGE + hotelID, null);                 // Remove all the notification of package
            clearNotification(hotelID, 0);                                      // clear notification on server
            int totalNewsBadgeCount = store.getInt(Constants.HOTEL_PUSH_COUNT, 0);          // get the total notification badge count for Hotels
            store.setInt(Constants.HOTEL_PUSH_COUNT, totalNewsBadgeCount - store.getInt(hotelID + "", 0));      // Subtract this hotel notification count from total notification count
            store.setInt(hotelID + "", 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_pros_detail_rates_offer);
        getSupportActionBar().hide();
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        hotelID = getIntent().getIntExtra("HotelID", 0);
        selectedHotel = Select.from(HotelData.class).where(Condition.prop("hotel_id").eq(hotelID)).first();
        final Button btn = (Button) findViewById(R.id.share);
        btn.setText(getString(R.string.htl_share_btn));
        btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        lv = (ListView) findViewById(R.id.lvProsRateOffer);
        if (selectedHotel != null) {
            url = selectedHotel.package_url + "&sectoken=" + Constants.md5();
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
            url = Constants.urlHotelPackage + "?client=" + Constants.clientId + "&sectoken=" + Constants.md5() + "&language=" + Constants.getLanguage() + "&hotel=" + hotelID;
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
        // setUpListView();
        lv.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        lv.setItemsCanFocus(true);
        final int rt = R.drawable.pressed;
        final int bt = R.drawable.btn_round;
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adp.setsel(position);
                selected = adp.getItem(position);
                btn.setBackgroundResource(bt);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != null) {
                    String body = "";
                    if (selectedHotel != null)
                        body += selectedHotel.name + ", ";
                    else
                        body += getIntent().getStringExtra("Name") + ", ";
                    body += selected.name + "\n";
                    body += selected.subTitle + "\n";
                    body += selected.desc + "\n";
                    appShare(HotelPackageActivity.this, body);
                }
            }
        });
    }

    private void loadOflineData() {
        rateList = Select.from(HotelRatesData.class).where(Condition.prop("hotel_id").eq(hotelID)).list();
        adp = new com.golfapp.test.adapters.AdapterHotelPackage(this, rateList, true, notificationString);
        lv.setAdapter(adp);
    }

    private void getNews() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
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
                    JSONArray array = jsonObject.getJSONArray("packages");
                    for (int a = 0; a < array.length(); a++) {
                        JSONObject obj = array.getJSONObject(a);
                        int rateID = obj.getInt("id");
                        String name = obj.getString("name");
                        String pubdate = obj.getString("pubdate");
                        String descr = obj.getString("descr");
                        String subtitle = obj.getString("subtitle");
                        HotelRatesData rate = Select.from(HotelRatesData.class).where(Condition.prop("hotel_id").eq(hotelID)
                                , Condition.prop("rate_id").eq(rateID)).first();
                        if (rate == null) {
                            rate = new HotelRatesData(hotelID, rateID, name, descr, subtitle, pubdate);
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
                    adp = new AdapterHotelPackage(HotelPackageActivity.this, rateList, true, notificationString);
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
            startActivity(new Intent(HotelPackageActivity.this, com.golfapp.test.activities.HotelsActivity.class).putExtra("Push", true));
            finish();
        } else {
            finish();
        }
    }


    private void setupActionbar() {
        ((TextView) findViewById(R.id.prosOfferActionTitle)).setText(R.string.htl_package_list_nav_bar);
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
                startActivity(new Intent(getApplicationContext(), com.golfapp.test.activities.MainActivity.class));
            }
        });
    }
}
