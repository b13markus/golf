package com.golfapp.test.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.AppConstants;
import com.golfapp.test.R;
import com.golfapp.test.adapters.AdapterCourse;
import com.golfapp.test.datafiles.CoursesData;
import com.golfapp.test.datafiles.ImageData;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.MyListView;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Golakiya on 7/4/2015.
 */
public class CourseActivity extends BaseActivity {

    private static final int MY_SOCKET_TIMEOUT_MS = 100 * 30;
    private MyListView lv;
    private List<CoursesData> list = new ArrayList<>();
    private AdapterCourse adapterPros;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int pageNumber = 0;
    private boolean isLoading = false;
    private int total;
    private boolean clearList = false;
    private View footerView;
    private String urlCourse = "";
    private static boolean loadItems = false;

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        if (adapterPros != null) {
            adapterPros.notifyDataSetChanged();
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
        setContentView(R.layout.activity_course);
        urlCourse = Constants.urlCourseData + "?client=" + Constants.clientId + "&sectoken=" + Constants.md5() + "&language=" +
                Constants.getLanguage() + "&draw=" + Constants.draw + "&page=";
        loadItems = true;
        addToBack(this);
        ((TextView) findViewById(R.id.courseActionTitle)).setText(R.string.crs_the_course_list_nav_bar);
        ((TextView) findViewById(R.id.courseActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        getSupportActionBar().hide();
        clearList = true;
        lv = ((MyListView) findViewById(R.id.listnews));

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
        footerView = inflater.inflate(R.layout.footer_view, null, false);
        lv.addFooterView(footerView);
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
                                getCourses();
                            }
                        } else {
                            lv.removeFooterView(footerView);
                        }
                    }
                }
            }
        });
        lv.setOnItemClickListener(this);
        setupActionBar();
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
        ((ImageView) findViewById(R.id.courseActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((ImageView) findViewById(R.id.courseActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        loadOffline();
        if (isNetworkAvailable()) {
            clearList = true;
            pageNumber = 0;
            lv.setScrollable(false);
            getCourses();
        } else {
            toast(getString(R.string.no_inet));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void checkIfUpdateReuired(final int id) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlCourse + 1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getInt("success") == 1) {
                                total = jsonObject.getJSONObject("paging").getInt("total");
                                JSONArray prosArray = jsonObject.getJSONArray("courses");
                                if (prosArray.length() > 0) {
                                    if (id != prosArray.getJSONObject(0).getInt("id")) {        // top Course ID does not match with our top id means update is available.
                                        clearList = true;
                                        lv.addFooterView(footerView);
                                        pageNumber = 0;
                                        getCourses();
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
    public void networkUnavailable() {

    }

    private void loadOffline() {
        clearList = true;
        list =  CoursesData.findWithQuery(CoursesData.class, "SELECT * FROM COURSES_DATA ORDER BY position", null);
        for (int a = 0; a < list.size(); a++) {
            list.get(a).imageList = Select.from(ImageData.class).where(Condition.prop("course_id").eq(list.get(a).courseID)).list();
        }
        setListView(false);
    }

    private void setListView(boolean openFirstPage) {
        if ((list.size() == 1 && openFirstPage) || !isNetworkAvailable()) {
            AppConstants.currentCourse = 0;
            Intent it = new Intent(CourseActivity.this, CourseDetailActivity.class);
            CoursesData pro = list.get(0);
            it.putExtra("CourseID", pro.courseID);
            //it.putExtra("rat",rat);
            startActivity(it);
            finish();
        } else {
            if (clearList) {
                clearList = false;
                adapterPros = new AdapterCourse(this, list);
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
            getCourses();
        } else {                // means coming back from detail page
            loadItems = true;       // make it true
        }
    }

    private void getCourses() {
        pageNumber += 1;
        isLoading = true;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlCourse + pageNumber, null,
                this, this);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        applicationInstance.addToRequestQueue(jsonObjReq, "Pros");
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        super.onResponse(jsonObject);
        if(pageNumber == 1){
            CoursesData.deleteAll(CoursesData.class);
        }
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
                    JSONArray prosArray = jsonObject.getJSONArray("courses");
                    for (int a = 0; a < prosArray.length(); a++) {
                        JSONObject obj = prosArray.getJSONObject(a);
                        int courseID = obj.getInt("id");
                        String name = obj.getString("name");
                        String descr = obj.getString("descr");
                        String holes = obj.getString("holes");
                        String par = obj.getString("par");
                        String length = obj.getString("length");
                        String length_unit = obj.getString("length_unit");
                        String facilities = obj.getString("facilities");
                        int position = obj.getInt("position");
                        int rate_count = obj.getInt("rate_count");
                        String rate_url = obj.getString("rate_url");
                        CoursesData course = Select.from(CoursesData.class).where(Condition.prop("course_id").eq(courseID)).first();
                        if (course == null) {
                            course = new CoursesData(courseID, position, rate_count, name, descr, holes, par, length, length_unit, rate_url, facilities);
                            course.save();
                        } else {
                            course.name = name;
                            course.facilities = facilities;
                            course.position = position;
                            course.rateCount = rate_count;
                            course.descr = descr;
                            course.holes = holes;
                            course.par = par;
                            course.lengthUnit = length_unit;
                            course.rateUrl = rate_url;
                            course.save();
                        }
                        ImageData.deleteAll(ImageData.class, "course_id = ? ", courseID + "");
                        JSONArray imagesArray = obj.getJSONArray("images");
                        for (int b = 0; b < imagesArray.length(); b++) {
                            JSONObject imageObj = imagesArray.getJSONObject(b);
                            String Imagename = imageObj.getString("name");
                            String url = imageObj.getString("url");
                            ImageData.deleteAll(ImageData.class, "course_id = ? and name = ? and url = ? ", courseID + "", Imagename, url);
                            ImageData image = new ImageData(0, 0, courseID, 0, 0, 0, Imagename, url);
                            image.save();
                            course.imageList.add(image);
                        }
                        list.add(course);
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
                    setListView(true);
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        super.onErrorResponse(volleyError);
        lv.removeFooterView(footerView);
        swipeRefreshLayout.setRefreshing(false);
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
                Intent it = new Intent(CourseActivity.this, CourseDetailActivity.class);
                CoursesData pro = list.get(position);
                it.putExtra("CourseID", pro.courseID);
                AppConstants.currentCourse = position;
                //it.putExtra("rat",rat);
                startActivity(it);
            }
        }
    }
}