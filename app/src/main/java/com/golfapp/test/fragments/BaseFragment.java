package com.golfapp.test.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import com.golfapp.test.activities.BaseActivity;
import com.golfapp.test.utils.NetworkStateReceiver;
import com.golfapp.test.utils.PrefStore;

/**
 * Created by aman on 11/24/2015.
 */
public class BaseFragment extends Fragment implements NetworkStateReceiver.NetworkStateReceiverListener, SwipeRefreshLayout.OnRefreshListener,
        View.OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener, AdapterView.OnItemClickListener {

    public BaseActivity baseActivity;
    public PrefStore store;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        baseActivity = (BaseActivity) getActivity();
        store = new PrefStore(baseActivity);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {

    }

    @Override
    public void onResponse(JSONObject jsonObject) {

    }

    @Override
    public void networkAvailable() {

    }

    @Override
    public void networkUnavailable() {

    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onRefresh() {

    }
}
