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
import com.golfapp.test.adapters.ProShopRatesAdapter;
import com.golfapp.test.datafiles.ProShopRatesData;
import com.golfapp.test.datafiles.ProshopData;
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
public class ProshopRateOfferActivity extends BaseActivity {

    private static final int MY_SOCKET_TIMEOUT_MS = 100 * 30;
    private static String urlNews = null;
    private ListView lv;
    private ProShopRatesData selected;
    private boolean frompush = false;
    private ProShopRatesAdapter adp;
    private int proShopID;
    private ProshopData selectedProShop;
    private List<ProShopRatesData> rateList = new ArrayList<>();
    private String notificationString;
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
        if (store.getString(Constants.PACKAGE + proShopID) != null) {               //get notification count for this proshop
            notificationString = store.getString(Constants.PACKAGE + proShopID);        // get offers having notification
            store.setString(Constants.PACKAGE + proShopID, null);                       // update all ofers notification to be seen
            clearNotification(proShopID, 0);                                            // clear notification on server
            int totalNewsBadgeCount = store.getInt(Constants.PROSHOP_PUSH_COUNT, 0);       // total notification count for all pros
            store.setInt(Constants.PROSHOP_PUSH_COUNT, totalNewsBadgeCount - store.getInt(proShopID + "", 0));     // update total pros notification count not seen
            store.setInt(proShopID + "", 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pros_detail_rates_offer);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);
        getSupportActionBar().hide();
        proShopID = getIntent().getIntExtra("ProShopID", 0);
        addToBack(this);
        selectedProShop = Select.from(ProshopData.class).where(Condition.prop("pro_shop_id").eq(proShopID)).first();
        setupActionbar();
        final Button btn = (Button) findViewById(R.id.share);
        btn.setText(getString(R.string.ps_share_btn));
        btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        lv = (ListView) findViewById(R.id.lvProsRateOffer);
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
                    if (selectedProShop != null)
                        body += selectedProShop.name + ", ";
                    else
                        body += getIntent().getStringExtra("Name") + ", ";
                    body += selected.name + "\n";
                    body += selected.subTitle + "\n";
                    body += selected.desc + "\n";

                    appShare(ProshopRateOfferActivity.this, body);
                }
            }
        });
        if (selectedProShop != null) {
            urlNews = selectedProShop.package_url;
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
            frompush = true;
            urlNews = Constants.urlProShopOffer + "?client=" + Constants.clientId + "&language=" + Constants.getLanguage() + "&proshop=" + proShopID;
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

    }

    private void loadOflineData() {
        rateList = Select.from(ProShopRatesData.class).where(Condition.prop("pro_shop_id").eq(proShopID)).list();
        adp = new ProShopRatesAdapter(this, rateList, true, notificationString);
        lv.setAdapter(adp);
    }

    private void getNews() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlNews+"&sectoken="+ Constants.md5(), null,
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
                        ProShopRatesData rate = Select.from(ProShopRatesData.class).where(Condition.prop("pro_shop_id").eq(proShopID)
                                , Condition.prop("rate_id").eq(rateID)).first();
                        if (rate == null) {
                            rate = new ProShopRatesData(proShopID, rateID, name, descr, subtitle, pubdate);
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
                    adp = new ProShopRatesAdapter(ProshopRateOfferActivity.this, rateList, true, notificationString);
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
            startActivity(new Intent(this, ProshopActivity.class).putExtra("Push", true));
            finish();
        } else {
            finish();
        }
    }


    private void setupActionbar() {
        ((TextView) findViewById(R.id.prosOfferActionTitle)).setText(R.string.ps_special_offer_nav_bar);
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

}
