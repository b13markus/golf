package com.golfapp.test.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.R;
import com.golfapp.test.RegistrationIntentService;
import com.golfapp.test.utils.ApplicationClass;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.NetworkStateReceiver;
import com.golfapp.test.utils.PrefStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity implements NetworkStateReceiver.NetworkStateReceiverListener,
        Response.Listener<JSONObject>, Response.ErrorListener {

    private String urlNews;
    private static final int SPLASH_TIME_OUT = 2000;
    private int remain = 2000;
    private boolean isTimeout = false;
    private int progress = 0;
    private ProgressBar splashProgress;
    private PrefStore store;
    private NetworkStateReceiver networkStateReceiver;

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
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.black));
        }
        super.onCreate(savedInstanceState);
        store = new PrefStore(this);
        store.setBoolean("IsStartup", true);
        setContentView(R.layout.activity_splash);
        splashProgress = (ProgressBar) findViewById(R.id.splashProgress);
        splashProgress.setMax(100);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        String appStartTime = store.getString(Constants.APP_START_TIME);
        if (appStartTime == null) {
            store.setString(Constants.APP_START_TIME, System.currentTimeMillis() + "");
        }
        urlNews = Constants.urlAdvertising + "?client=" + Constants.clientId + "&sectoken=" + Constants.md5() + "&language=" + Constants.getLanguage();
        if (isNetworkAvailable()) {
        } else {
            next();
        }
        new CountDownTimer(SPLASH_TIME_OUT, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                remain -= 1;
            }

            @Override
            public void onFinish() {
                isTimeout = true;
            }
        }.start();
        if (checkPlayServices()) {
            if (store.getInt(Constants.REG_ID, 0) == 0) {// check if GCM is already registered.
                Intent intent = new Intent(this, RegistrationIntentService.class);
                new PrefStore(this, Constants.OLD_LANGUAGE).setInt(Constants.OLD_LANGUAGE, Constants.getLanguage());
                startService(intent);
            } else {
                if (new PrefStore(this, Constants.OLD_LANGUAGE).getInt(Constants.OLD_LANGUAGE, 0) != Constants.getLanguage()) {
                    new PrefStore(this, Constants.OLD_LANGUAGE).setInt(Constants.OLD_LANGUAGE, Constants.getLanguage());
                    Intent intent = new Intent(this, RegistrationIntentService.class);
                    startService(intent);
                }
            }
        } else {
        }
        getProfile();
        Timer timer = new Timer();
        timer.schedule(task, 350, 350);
    }

    TimerTask task = new TimerTask() {
        @Override
        public void run() {
            progress = progress + 10;
            splashProgress.setProgress(progress);
        }
    };


    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("Play ", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    ApplicationClass applicationInstance;

    private void getProfile() {
        applicationInstance = (ApplicationClass) getApplication();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlNews, null,
                this, this);
        applicationInstance.addToRequestQueue(jsonObjReq, "Profile");
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        store.setString(Constants.PROFILE_STRING, jsonObject.toString());
        next();
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        next();
    }

    public void next() {
        runOnUiThread(new Runnable() {
            public void run() {
                if (store.getString(Constants.PROFILE_STRING) != null) {
                    if (isTimeout) {
                        gotoAdd();
                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                gotoAdd();
                            }
                        }, remain);
                    }
                } else {

                }
            }
        });
    }

    void gotoAdd() {
        try {
            if (store.getString(Constants.PROFILE_STRING) != null) {
                JSONObject obj = new JSONObject(store.getString(Constants.PROFILE_STRING));
                JSONObject advert = obj.getJSONObject("advertisemet");
                String url = advert.getString("image");
                if (!url.equals("") && !url.equals("null")) {
                    task.cancel();
                    splashProgress.setProgress(100);
                    Intent it = new Intent(SplashActivity.this, MainActivity.class);
                    it.putExtra("ShowAdd", true);
                    startActivity(it);
                    finish();
                } else {
                    task.cancel();
                    splashProgress.setProgress(100);
                    Intent it = new Intent(SplashActivity.this, MainActivity.class);
                    it.putExtra("ShowAdd", true);
                    startActivity(it);
                    finish();
                }
            } else {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void networkAvailable() {
        getProfile();
    }


    @Override
    public void networkUnavailable() {

    }
}
