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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.R;
import com.golfapp.test.adapters.AdpNews;
import com.golfapp.test.datafiles.ImageData;
import com.golfapp.test.datafiles.NewsData;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.MyListView;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends BaseActivity {
    private static final int MY_SOCKET_TIMEOUT_MS = 100 * 30;
    private MyListView lv;
    private AdpNews adapterNews;
    int i = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int pageNumber = 0;
    private List<NewsData> newsList = new ArrayList<>();
    boolean isLoading = false;
    private int total;
    private boolean clearList = false;
    private View footerView;
    private String urlNews;
    private static boolean loadItems = false;
    private ArrayList<NewsData> updatedNews = new ArrayList<>();

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        if (adapterNews != null) {
            adapterNews.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        loadItems = true;
        addToBack(this);
        urlNews = Constants.urlNews + "?client=" + Constants.clientId + "&language=" + Constants.getLanguage() + "&draw=" + Constants.draw + "&page=";
        getSupportActionBar().hide();
        lv = (MyListView) findViewById(R.id.listnews);
        footerView = ((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_view, null, false);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);
        loadOffline();
        if (isNetworkAvailable()) {
            swipeRefreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        } else {
            toast(getString(R.string.no_inet));
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
                        if (newsList.size() < total) {
                            if (isNetworkAvailable()) {
                                lv.addFooterView(footerView);
                                getNews();
                            }
                        } else {
                            lv.removeFooterView(footerView);
                        }
                    }
                }
            }
        });
        clearList = true;
        setupActionbar();
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

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadOffline();
        if (isNetworkAvailable()) {
            clearList = true;
            pageNumber = 0;
            lv.setScrollable(false);
            getNews();
        } else {
            toast(getString(R.string.no_inet));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void networkUnavailable() {

    }

    @Override
    public void networkAvailable() {
        if (loadItems) {            // if true means coming from home screen or internet is available now
            pageNumber = 0;
            clearList = true;
            getNews();
        } else {                // means coming back from detail page
            loadItems = true;       // make it true
        }
    }

    private void getNews() {
        pageNumber += 1;
        isLoading = true;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlNews + pageNumber, null,
                this, this);


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        applicationInstance.addToRequestQueue(jsonObjReq, "News");
    }

    private void loadOffline() {
        clearList = true;
//        newsList = NewsData.listAll(NewsData.class);
        newsList = Select.from(NewsData.class).orderBy("updated DESC").list();
//        newsList = NewsData.findWithQuery(NewsData.class, "SELECT * FROM NEWS_DATA ORDER BY updated DESC", null);
        for (int a = 0; a < newsList.size(); a++) {
            newsList.get(a).imageList = Select.from(ImageData.class).where(Condition.prop("news_id").eq(newsList.get(a).newsID)).list();
        }
        setListView(false);
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
                if (jsonObject.getInt("success") == 1) {
                    if (clearList) {
                        updatedNews.clear();
                    }
                    JSONArray newsArray = jsonObject.getJSONArray("news");
                    total = jsonObject.getJSONObject("paging").getInt("total");
                    for (int a = 0; a < newsArray.length(); a++) {
                        JSONObject obj = newsArray.getJSONObject(a);
                        int newsID = obj.getInt("id");
                        String title = obj.getString("title");
                        String subtitle = obj.getString("subtitle");
                        String descr = obj.getString("descr");
                        String pubdate = obj.getString("pubdate");
                        String updated = obj.getString("updated");
                        NewsData news = Select.from(NewsData.class).where(Condition.prop("news_id").eq(newsID)).first();
                        if (news == null) {
                            news = new NewsData(newsID, title, subtitle, descr, pubdate, updated);
                            news.save();
                        } else {
                            news.title = title;
                            news.desc = descr;
                            news.pubDate = pubdate;
                            news.updated = updated;
                            news.subtitle = subtitle;
                            news.save();
                        }
                        ImageData.deleteAll(ImageData.class, "news_id = ? ", newsID + "");
                        JSONArray imagesArray = obj.getJSONArray("images");
                        for (int b = 0; b < imagesArray.length(); b++) {
                            JSONObject imageObj = imagesArray.getJSONObject(b);
                            String Imagename = imageObj.getString("name");
                            String url = imageObj.getString("url");
                            ImageData.deleteAll(ImageData.class, "news_id = ? and name = ? and url = ? ", newsID + "", Imagename, url);
                            ImageData image = new ImageData(0, newsID, 0, 0, 0, 0, Imagename, url);
                            image.save();
                            news.imageList.add(image);
                        }
                        updatedNews.add(news);
                    }
                    newsList = updatedNews;
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
                    setListView(true);
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }


    private void setListView(boolean isFirstPage) {
        if ((newsList.size() == 1 && isFirstPage)  || (!isNetworkAvailable() && newsList.size() == 1)) {
            Intent it = new Intent(NewsActivity.this, NewsDetailActivity.class);
            NewsData pro = newsList.get(0);
            it.putExtra("NewsID", pro.newsID);
            //it.putExtra("rat",rat);
            startActivity(it);
            finish();
        } else {
            if (clearList) {
                clearList = false;
                adapterNews = new AdpNews(this, newsList);
                lv.addFooterView(footerView);
                lv.setAdapter(adapterNews);
            } else {
                adapterNews.notifyDataSetChanged();
            }
        }
        swipeRefreshLayout.setRefreshing(false);
        lv.removeFooterView(footerView);
        lv.setScrollable(true);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        super.onErrorResponse(volleyError);
        lv.removeFooterView(footerView);
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.newsActionTitle)).setText(getString(R.string.nws_nav_bar));
        ((TextView) findViewById(R.id.newsActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.newsActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (true) {
                    onBackPressed();
                } else {
                    Intent it = new Intent(NewsActivity.this, MainActivity.class);
                    startActivity(it);
                    finish();
                }
            }
        });

        ((ImageView) findViewById(R.id.newsActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (!swipeRefreshLayout.isRefreshing()) {
            if (!isLoading) {
                if (isNetworkAvailable()) {
                    loadItems = false;
                } else {
                    toast(getString(R.string.no_inet));
                }
                Intent it = new Intent(NewsActivity.this, NewsDetailActivity.class);
                NewsData pro = newsList.get(position);
                it.putExtra("NewsID", pro.newsID);
                //it.putExtra("rat",rat);
                startActivity(it);
            }
        }
    }
}
