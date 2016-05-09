package com.golfapp.test.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.R;
import com.golfapp.test.adapters.AdapterPros;
import com.golfapp.test.datafiles.ImageData;
import com.golfapp.test.datafiles.ProsData;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.MyListView;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aman on 11/21/2015.
 */
public class ProsActivity extends BaseActivity {


    private MyListView lv;
    private String urlPros;
    private List<ProsData> list = new ArrayList<>();
    private AdapterPros adapterPros;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int pageNumber = 0;
    private boolean isLoading = false;
    private int total;
    private boolean clearList = false;
    private View footerView;
    static boolean loadItems = false;               //used to refresh list when internet is back            true coming from home screen

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        if (adapterPros != null) {
            adapterPros.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pros);
        loadItems = true;
        getSupportActionBar().hide();
        ((TextView) findViewById(R.id.prosActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        lv = (MyListView) findViewById(R.id.lvPros);
        ((TextView) findViewById(R.id.prosActionTitle)).setText(getString(R.string.pro_list_nav_bar));
        setupActionBar();
        clearList = true;
        footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view, null, false);
        lv.addFooterView(footerView);
        urlPros = Constants.urlProDetail + "?client=" + Constants.clientId + "&language=" +
                Constants.getLanguage()+"&sectoken="+ Constants.md5() + "&draw=" + Constants.draw + "&page=";
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);

        if (!isNetworkAvailable()) {
            loadOffline();
        } else {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
        lv.setOnItemClickListener(this);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //what is the bottom iten that is visible
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if (lastInScreen != 0) {
                    if ((lastInScreen == totalItemCount) && !(isLoading)) {// && !(loadingMore)){
                        if (list.size() < total) {
                            if (isNetworkAvailable()) {
                                lv.addFooterView(footerView);
                                getPros();
                            }
                        } else {
                            lv.removeFooterView(footerView);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("Push", false)) {
            startActivity(new Intent(this, MainActivity.class).putExtra("Push", true));
            finish();
        } else {
            finish();
        }
    }

    private void setupActionBar() {
        ((ImageView) findViewById(R.id.prosActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((ImageView) findViewById(R.id.prosActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (isNetworkAvailable()) {
            clearList = true;
            pageNumber = 0;
            lv.setScrollable(false);
            getPros();
        } else {
            toast(getString(R.string.no_inet));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void loadOffline() {
        clearList = true;
        list = ProsData.listAll(ProsData.class);
        for (int a = 0; a < list.size(); a++) {
            list.get(a).imageList = Select.from(ImageData.class).where(Condition.prop("pros_id").eq(list.get(a).prosID)).list();
        }
        setListView();
    }

    private void setListView() {
        if (list.size() == 1) {
            Intent it = new Intent(ProsActivity.this, ProsDetailActivity.class);
            ProsData pro = list.get(0);
            it.putExtra("ProsID", pro.prosID);
            //it.putExtra("rat",rat);
            startActivity(it);
            finish();
        } else {
            if (clearList) {
                clearList = false;
                adapterPros = new AdapterPros(this, list);
                lv.setAdapter(adapterPros);
            } else {
                adapterPros.notifyDataSetChanged();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
        lv.removeFooterView(footerView);
        lv.setScrollable(true);
    }

    @Override
    public void networkAvailable() {
        if (loadItems) {            // if true means coming from home screen or internet is available now
            pageNumber = 0;
            clearList = true;
            getPros();
        } else {                // means coming back from detail page
            loadItems = true;       // make it true
        }
    }

    private void getPros() {
        pageNumber += 1;
        isLoading = true;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlPros + pageNumber, null,
                this, this);
        applicationInstance.addToRequestQueue(jsonObjReq, "Pros");
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
                    JSONArray prosArray = jsonObject.getJSONArray("pros");
                    for (int a = 0; a < prosArray.length(); a++) {
                        JSONObject obj = prosArray.getJSONObject(a);
                        int prosID = obj.getInt("id");
                        String name = obj.getString("name");
                        String descr = obj.getString("descr");
                        String phone = obj.getString("phone");
                        String email = obj.getString("email");
                        String website = obj.getString("website");
                        String address = obj.getString("address");
                        String streetno = obj.getString("streetno");
                        String route = obj.getString("route");
                        String city = obj.getString("city");
                        String state = obj.getString("state");
                        String postalcode = obj.getString("postalcode");
                        String country = obj.getString("country");
                        double longitude = obj.optDouble("longitude");
                        double latitude = obj.optDouble("latitude");
                        int position = obj.getInt("position");
                        int package_count = obj.getInt("package_count");
                        String package_url = obj.getString("package_url");
                        ProsData pros = Select.from(ProsData.class).where(Condition.prop("pros_id").eq(prosID)).first();
                        if (pros == null) {
                            pros = new ProsData(prosID, position, package_count, longitude, latitude, name
                                    , descr, phone, email, website, address, streetno, route, city, state, postalcode, country, package_url);
                            pros.save();
                        } else {
                            pros.position = position;
                            pros.package_count = package_count;
                            pros.latitude = latitude;
                            pros.longitude = longitude;
                            pros.name = name;
                            pros.desc = descr;
                            pros.phone = phone;
                            pros.email = email;
                            pros.website = website;
                            pros.address = address;
                            pros.streetno = streetno;
                            pros.route = route;
                            pros.city = city;
                            pros.state = state;
                            pros.postalcode = postalcode;
                            pros.country = country;
                            pros.package_url = package_url;
                            pros.save();

                        }
                        JSONArray imagesArray = obj.getJSONArray("images");
                        ImageData.deleteAll(ImageData.class, "pros_id = ? ", prosID + "");
                        for (int b = 0; b < imagesArray.length(); b++) {
                            JSONObject imageObj = imagesArray.getJSONObject(b);
                            String Imagename = imageObj.getString("name");
                            String url = imageObj.getString("url");
                            ImageData.deleteAll(ImageData.class, "pros_id = ? and name = ? and url = ? ", prosID + "", Imagename, url);
                            ImageData image = new ImageData(prosID, 0, 0, 0, 0, 0, Imagename, url);
                            image.save();
                            pros.imageList.add(image);
                        }
                        list.add(pros);
                    }
                }
            } catch (Exception e) {
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
                    setListView();
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        super.onErrorResponse(volleyError);
        swipeRefreshLayout.setRefreshing(false);
        lv.removeFooterView(footerView);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!swipeRefreshLayout.isRefreshing()) {
            if (!isLoading) {
                if (isNetworkAvailable()) {
                    loadItems = false;
                }
                Intent it = new Intent(ProsActivity.this, ProsDetailActivity.class);
                ProsData pro = list.get(position);
                it.putExtra("ProsID", pro.prosID);
                //it.putExtra("rat",rat);
                startActivity(it);
            }
        }
    }
}
