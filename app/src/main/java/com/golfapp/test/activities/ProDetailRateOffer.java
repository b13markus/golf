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
import com.golfapp.test.adapters.AdapterProsRateOffer;
import com.golfapp.test.datafiles.ProRates;
import com.golfapp.test.datafiles.ProsData;
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
public class ProDetailRateOffer extends BaseActivity {

    private static final int MY_SOCKET_TIMEOUT_MS = 100 * 30;
    private static String urlNews = null;
    private ListView lv;
    private ProRates selected;
    private boolean frompush = false;
    private AdapterProsRateOffer adp;
    private int prosID;
    private ProsData selectedPros;
    private List<ProRates> rateList = new ArrayList<>();
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
        if (store.getString(Constants.PACKAGE + prosID) != null) {          // is there any notification for this pro
            notificationString = store.getString(Constants.PACKAGE + prosID);           // get rates which has notification
            store.setString(Constants.PACKAGE + prosID, null);                          // clear the notification for this pro on shared storage
            clearNotification(prosID, 0);                                               // clear notification on server
            int totalNewsBadgeCount = store.getInt(Constants.PROS_PUSH_COUNT, 0);       // get total notification bage count for pros
            store.setInt(Constants.PROS_PUSH_COUNT, totalNewsBadgeCount - store.getInt(prosID + "", 0));        // subtract this pros notification from total notification
            store.setInt(prosID + "", 0);
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
        prosID = getIntent().getIntExtra("ProsID", 0);
        selectedPros = Select.from(ProsData.class).where(Condition.prop("pros_id").eq(prosID)).first();
        urlNews = Constants.urlProRateOffer + "?client=" + Constants.clientId + "&sectoken=" + Constants.md5() + "&language=" + Constants.getLanguage() + "&pro=" + prosID;
        final Button btn = (Button) findViewById(R.id.share);
        btn.setText(getString(R.string.pro_share_btn));
        btn.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        lv = (ListView) findViewById(R.id.lvProsRateOffer);
        if (selectedPros != null) {
            if (isNetworkAvailable()) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                getNews();
            } else {
                loadOflineData();
            }
        } else {
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
                selected = (ProRates) adp.getItem(position);
                btn.setBackgroundResource(bt);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected != null) {
                    String body = "";
                    if (selectedPros != null)
                        body += selectedPros.name + ", ";
                    else
                        body += getIntent().getStringExtra("Name") + ", ";
                    body += selected.name + "\n";
                    body += selected.subTitle + "\n";
                    body += selected.desc + "\n";

                    appShare(ProDetailRateOffer.this, body);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (isNetworkAvailable()) {
            getNews();
        } else {
            toast(getString(R.string.no_inet));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void loadOflineData() {
        rateList = Select.from(ProRates.class).where(Condition.prop("pros_id").eq(prosID)).list();
        adp = new AdapterProsRateOffer(this, rateList, true, notificationString);
        lv.setAdapter(adp);
    }

    private void getNews() {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlNews, null,
                this, this);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        applicationInstance.addToRequestQueue(jsonObjReq, "News");

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
                        ProRates rate = Select.from(ProRates.class).where(Condition.prop("pros_id").eq(prosID)
                                , Condition.prop("rate_id").eq(rateID)).first();
                        if (rate == null) {
                            rate = new ProRates(prosID, rateID, name, descr, subtitle, pubdate);
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
                    adp = new AdapterProsRateOffer(ProDetailRateOffer.this, rateList, true, notificationString);
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
            startActivity(new Intent(this, ProsActivity.class).putExtra("Push", true));
            finish();
        } else {
            finish();
        }
    }


    private void setupActionbar() {
        ((TextView) findViewById(R.id.prosOfferActionTitle)).setText(R.string.pro_rate_offer_nav_bar);
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
