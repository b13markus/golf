package com.golfapp.test.fragments;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.utils.Constants;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import com.golfapp.test.R;
import com.golfapp.test.adapters.AdapterEvent;
import com.golfapp.test.datafiles.EventsData;
import com.golfapp.test.utils.MyListView;

/**
 * Created by aman on 11/24/2015.
 */
public class UpcomingEventsFragment extends BaseFragment {

    MyListView lv;
    List<EventsData> list = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    int pageNumber = 0;
    boolean isLoading = false;
    private int total;
    private boolean clearList = false;
    AdapterEvent adapterEvent;
    private View footerView;
    private String urlUpComingEvent;
    private TextView emptyTextTV;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.events_fragments, null);
        lv = (MyListView) view.findViewById(R.id.listnews);
        emptyTextTV = (TextView) view.findViewById(R.id.emptyTextTV);
        emptyTextTV.setTypeface(Typeface.createFromAsset(baseActivity.getAssets(), "fonts/B.ttf"));
        urlUpComingEvent = Constants.urlUpcomingEvents + "?client=" + Constants.clientId + "&language=" + Constants.getLanguage()
                + "&sectoken=" + Constants.md5() + "&category=future" + "&draw=" + Constants.draw + "&page=";
        clearList = true;
        footerView = baseActivity.inflater.inflate(R.layout.footer_view, null, false);
        lv.addFooterView(footerView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorScheme(android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light,
                android.R.color.holo_green_light);
        if (!baseActivity.isNetworkAvailable()) {
            baseActivity.toast(getString(R.string.no_inet));
            loadOffline();
        } else {
            if (pageNumber == 0) {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(true);
                    }
                });
                getUpcomingNews();
            }
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
                            if (baseActivity.isNetworkAvailable()) {
                                lv.addFooterView(footerView);
                                getUpcomingNews();
                            }
                        } else {
                            lv.removeFooterView(footerView);
                        }
                    }
                }
                int topRowVerticalPosition = (lv == null || lv.getChildCount() == 0) ?
                        0 : lv.getChildAt(0).getTop();
                swipeRefreshLayout.setEnabled((topRowVerticalPosition >= 0));
            }
        });
        return view;
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        /*if (baseActivity.isNetworkAvailable()) {
            if (list.size() > 0)
                checkIfUpdateReuired(list.get(0).eventID);
            else
                swipeRefreshLayout.setRefreshing(false);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }*/
        if (baseActivity.isNetworkAvailable()) {
            clearList = true;
            pageNumber = 0;
            lv.setScrollable(false);
            getUpcomingNews();
        } else {
            baseActivity.toast(getString(R.string.no_inet));
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void checkIfUpdateReuired(final int prosID) {
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlUpComingEvent + 1, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            if (jsonObject.getInt("success") == 1) {
                                total = jsonObject.getJSONObject("paging").getInt("total");
                                JSONArray prosArray = jsonObject.getJSONArray("events");
                                if (prosArray.length() > 0) {
                                    if (prosID != prosArray.getJSONObject(0).getInt("id")) {        // top Pros ID does not match with our top id means update is available.
                                        clearList = true;
                                        lv.addFooterView(footerView);
                                        pageNumber = 0;
                                        getUpcomingNews();
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
        baseActivity.applicationInstance.addToRequestQueue(jsonObjReq, "Pros");
    }


    @Override
    public void networkUnavailable() {
        getUpcomingNews();
    }

    private void loadOffline() {
        clearList = true;
        list = Select.from(EventsData.class).where(Condition.prop("is_upcoming_event").eq(1)).list();
        setListView();
    }

    private void setListView() {
        try {
            if (clearList) {
                clearList = false;
                adapterEvent = new AdapterEvent(baseActivity, list);
                lv.setAdapter(adapterEvent);
            } else {
                adapterEvent.notifyDataSetChanged();
            }
            if (list.size() == 0) {
                emptyTextTV.setVisibility(View.VISIBLE);
                emptyTextTV.setText(getResources().getString(R.string.evt_no_up_coming));
                lv.setVisibility(View.GONE);
            } else {
                emptyTextTV.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            }
            swipeRefreshLayout.setRefreshing(false);
            lv.removeFooterView(footerView);
            lv.setScrollable(true);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void networkAvailable() {
        getUpcomingNews();
    }

    private void getUpcomingNews() {
        if (pageNumber == 0) {
            EventsData.deleteAll(EventsData.class, "is_upcoming_event = ?", "1");
        }
        pageNumber += 1;
        isLoading = true;
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlUpComingEvent + pageNumber, null,
                this, this);
        baseActivity.applicationInstance.addToRequestQueue(jsonObjReq, "Pros");
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        super.onResponse(jsonObject);
        new SaveData().execute(jsonObject);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        super.onErrorResponse(volleyError);
        lv.removeFooterView(footerView);
        swipeRefreshLayout.setRefreshing(false);
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
                    JSONArray prosArray = jsonObject.getJSONArray("events");
                    for (int a = 0; a < prosArray.length(); a++) {
                        JSONObject obj = prosArray.getJSONObject(a);
                        int eventID = obj.getInt("id");
                        String event_date = obj.getString("event_date");
                        String name = obj.getString("name");
                        String format = obj.getString("format");
                        String remark1 = obj.getString("remark1");
                        String remark2 = obj.getString("remark2");
                        String file_detail = obj.getString("file_detail");
                        String file_teetime = obj.getString("file_teetime");
                        String file_result = obj.getString("file_result");
                        String file_default = obj.getString("file_default");
                        String pubdate = obj.getString("pubdate");

                        EventsData event = Select.from(EventsData.class).where(Condition.prop("event_id").eq(eventID)).first();
                        if (event == null) {
                            event = new EventsData(eventID, 1, name, event_date, format, remark1,
                                    file_detail, remark2, file_teetime, file_result, file_default, pubdate);
                            event.save();
                        } else {
                            event.name = name;
                            event.eventDate = event_date;
                            event.fileDefault = file_default;
                            event.format = format;
                            event.isUpcomingEvent = 1;
                            event.remark1 = remark1;
                            event.fileDetail = file_detail;
                            event.remark2 = remark2;
                            event.fileTeeTime = file_teetime;
                            event.fileResult = file_result;
                            event.pubdate = pubdate;
                            event.save();
                        }
                        list.add(event);
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
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setListView();
                    isLoading = false;
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }
    }

}
