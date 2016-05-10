package com.golfapp.test.activities;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.R;
import com.golfapp.test.RegistrationIntentService;
import com.golfapp.test.utils.ApplicationClass;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.CustomRequest;
import com.golfapp.test.utils.NetworkStateReceiver;
import com.golfapp.test.utils.PrefStore;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by aman on 11/21/2015.
 */
public class BaseActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener, SwipeRefreshLayout.OnRefreshListener,
        OnClickListener, Response.Listener<JSONObject>, Response.ErrorListener, AdapterView.OnItemClickListener {

    public PrefStore store;
    public LayoutInflater inflater;
    public ApplicationClass applicationInstance;
    private NetworkStateReceiver networkStateReceiver;
    private JSONObject obj;
    public ImageLoader il;
    public DisplayImageOptions options;
    public static boolean showAdvertisements = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        store = new PrefStore(this);
        applicationInstance = ApplicationClass.getInstance();
        il = ImageLoader.getInstance();
        il.init(ImageLoaderConfiguration.createDefault(applicationInstance));
        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheOnDisc(true)
                .cacheInMemory(true)
                .build();
        inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        networkStateReceiver = new NetworkStateReceiver();                              //used to get connection changes if internet is up or down
        networkStateReceiver.addListener(this);
        try {
            if (store.getString(Constants.PROFILE_STRING) != null) {
                obj = new JSONObject(store.getString(Constants.PROFILE_STRING));
                imageUrl = obj.getJSONObject("advertisemet").getString("image");
                adUrl = obj.getJSONObject("advertisemet").getString("url");
                adName = obj.getJSONObject("advertisemet").getString("name");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
    protected void onResume() {
        super.onResume();
        md5();
        store = new PrefStore(this);
        store.setBoolean("StartKilling", false);                // when activity is resumed stop the killing flag
        store.setInt(Constants.IS_APPLICATION_VISIBLE, 1);          // Now app is visible for push notifications update home screen
        if (isNetworkAvailable()) {
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
            // if internet is up and ap comes from background update the User Profile data and show advertisement
            getProfile();
        } else {
            displayAdvertisement();                 // internet is down and app comes from background or is started show cached advertisement
        }
        registerReceiver(pushNotificationReceiver, new IntentFilter(Constants.PUSH_RECEIVED));
    }

    private void getProfile() {
        String urlNews = Constants.urlAdvertising + "?client=" + Constants.clientId
                + "&sectoken=" + Constants.md5()
                + "&language=" + Constants.getLanguage();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlNews, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        store.setString(Constants.PROFILE_STRING, jsonObject.toString());
                        displayAdvertisement();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                displayAdvertisement();
            }
        });
        applicationInstance.addToRequestQueue(jsonObjReq, "Profile");
    }

    private String imageUrl;
    private String adUrl;
    private String adName;

    public long getTimeDifference(Long startTime, Long endTime) {
        long processTime = endTime - startTime;
        long days = processTime / 86400000L;
        if (days > 0)
            return days * 24 * 60 * 60;
        processTime -= days * 86400000L;
        long hours = processTime / 3600000L;
        if (hours > 0)
            return hours * 60 * 60;
        processTime -= hours * 3600000L;
        long mins = processTime / 60000L;
        if (mins > 0)
            return mins * 60;
        processTime -= mins * 60000L;
        long seconds = processTime / 1000L;
        return seconds;
       /* processTime -= seconds * 1000L;
        long milliSeconds = processTime;
        return seconds;*/
    }

    public void displayAdvertisement() {
        String appStartTime = store.getString(Constants.APP_START_TIME);
        if (appStartTime == null) {             // if app is firstime launched there is not time show advertisement and insert the time.
            store.setString(Constants.APP_START_TIME, System.currentTimeMillis() + "");
            showAdd();
            return;
        }

        long appStartTimeInt = Long.valueOf(appStartTime);
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - appStartTimeInt;
        long diffInSec = getTimeDifference(appStartTimeInt, currentTime);          //calculate the time difference

        showLogs("diffInSec", diffInSec + "");

        if (diffInSec >= Constants.AD_SERVING_TIME_IN_SEC) {            // if time difference is greater than the advertisement time show add
            showAdd();
        }
        store.setString(Constants.APP_START_TIME, System.currentTimeMillis() + "");
    }

    public String md5() {
        try {
            // Create MD5 Hash

            DateTimeZone denverTimeZone = DateTimeZone.forID("Europe/Zurich");
            DateTime denverDateTime = new DateTime(denverTimeZone);
            DateTimeFormatter fmt = DateTimeFormat.forPattern("dd-mm-yyyy hh.mm.ss");
            String dtStr = fmt.print(denverDateTime);
            dtStr = dtStr + "sacha31" + Constants.clientId;
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(dtStr.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void showAdd() {
        if (showAdvertisements) {
            try {
                JSONObject obj = new JSONObject(store.getString(Constants.PROFILE_STRING));
                imageUrl = obj.getJSONObject("advertisemet").getString("image");
                adUrl = obj.getJSONObject("advertisemet").getString("url");
                adName = obj.getJSONObject("advertisemet").getString("name");
                if (!imageUrl.equals("null")) {                         // if advertisement is not null for this language. Load image and show popup
                    DisplayImageOptions option = new DisplayImageOptions.Builder()
                            .cacheOnDisc(true)
                            .build();
                    il.loadImage(imageUrl, option, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            startActivity(new Intent(BaseActivity.this, Advertisement.class));
                            showAdvertisements = false;
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private BroadcastReceiver pushNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {         // Received Push notification when app is live.

            if (context instanceof MainActivity) {          // if current opened class is main screen
                ((MainActivity) context).setBadgeCount();       // update badges on main screen
            }
        }
    };

    private void updateBadgeOnAppIcon() {
        int totalNotifications = 0;
        totalNotifications = store.getInt(Constants.NEWS_PUSH_COUNT, 0) + store.getInt(Constants.HOTEL_PUSH_COUNT, 0)       // get total number of push stored.
                + store.getInt(Constants.PROSHOP_PUSH_COUNT, 0) + store.getInt(Constants.PROS_PUSH_COUNT, 0) +
                store.getInt(Constants.RESTAURANTS_PUSH_COUNT, 0);
        ShortcutBadger.with(getApplicationContext()).count(totalNotifications);             // update badge on app icon.
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateBadgeOnAppIcon();
        store.setString(Constants.KILL_BACKSTACK_TIME, System.currentTimeMillis() + "");
        store.setBoolean("StartKilling", true);                 // App is going in background update the kill backstack flag to true.
        unregisterReceiver(pushNotificationReceiver);
        store.setInt(Constants.IS_APPLICATION_VISIBLE, 0);
        store.setString(Constants.APP_START_TIME, System.currentTimeMillis() + "");     // Update the last visit time for Advertisement
    }

    public void clearNotification(final int id, final int clientId) {           // remove notification of the item.
        String url = Constants.removeNotifications;
        int registrationID = store.getInt(Constants.REG_ID, 0);
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        Map<String, String> params = new HashMap<String, String>();
        params.put("regid", registrationID + "");
        params.put("device_id", deviceId);
        if (clientId != 0) {
            params.put("sid", clientId + "");
            params.put("pid", id + "");
        } else {
            params.put("sid", id + "");
        }
        CustomRequest jsObjRequest = new CustomRequest(Request.Method.POST, url, params, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(id);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        applicationInstance.addToRequestQueue(jsObjRequest);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.registerReceiver(networkStateReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void appShare(Context context, String body) {
        body.replaceAll("null", "");
        String text = "";
        String profile = store.getString(Constants.PROFILE_STRING);
        if (profile != null) {
            try {
                JSONObject obj = new JSONObject(profile);
                String shareURL = obj.getJSONObject("profile").getString("android_url");
                if (shareURL != null) {
                    if (!shareURL.equals("")) {
                        text = obj.getString("client");
                        text = text.replaceAll("\\&#244;", "ô");
                    }
                    body = body + "\n\n\n" + text + " Mobile App \n" + shareURL;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, body);

        try {
            text = obj.getString("client");
            text = text.replaceAll("\\&#244;", "ô");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, text);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendIntent.setType("text/plain");
        context.startActivity(sendIntent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.unregisterReceiver(networkStateReceiver);
    }


    @Override
    public void networkAvailable() {
        updateData();
    }

    public void updateData() {

    }

    @Override
    public void networkUnavailable() {

    }

    public void toast(String msg) {
        Toast.makeText(BaseActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showLogs(String tag, String msg) {
        Log.e(tag, msg);
    }

    @Override
    public void onErrorResponse(VolleyError volleyError) {
        try {
            toast(getString(R.string.no_api));
            showLogs("Error", volleyError.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Callback loadImage(RoundedImageView ivUser, RoundedImageView placeHolderImage, String url, int placeHolder) {
        return new callback(ivUser, placeHolderImage, url, placeHolder);
    }

    public class callback implements Callback {

        private final int placeHolder;
        RoundedImageView imageView;
        RoundedImageView placeHolderImage;
        String url;

        public callback(RoundedImageView imageView, RoundedImageView placeHolderImage, String url, int placeHolder) {
            this.imageView = imageView;
            this.placeHolderImage = placeHolderImage;
            this.placeHolder = placeHolder;
            this.url = url;
        }

        @Override
        public void onSuccess() {

        }

        @Override
        public void onError() {
            final AtomicBoolean playAnimation = new AtomicBoolean(true);
            Picasso.with(BaseActivity.this)
                    .load(url)
                    .config(Bitmap.Config.RGB_565)
                    .placeholder(placeHolder)
                    .error(placeHolder)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if (playAnimation.get()) {
                                placeHolderImage.setVisibility(View.VISIBLE);
                                Animation fadeOut = new AlphaAnimation(0, 1);
                                fadeOut.setInterpolator(new AccelerateInterpolator());
                                fadeOut.setDuration(500);
                                imageView.startAnimation(fadeOut);

                                Animation fadeOutPlaceholder = new AlphaAnimation(1, 0);
                                fadeOutPlaceholder.setInterpolator(new AccelerateInterpolator());
                                fadeOutPlaceholder.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        placeHolderImage.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onAnimationRepeat(Animation animation) {

                                    }
                                });
                                fadeOutPlaceholder.setDuration(500);
                                placeHolderImage.startAnimation(fadeOutPlaceholder);
                            }
                            playAnimation.set(false);

                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    @Override
    public void onResponse(JSONObject jsonObject) {
        showLogs("Response", jsonObject.toString());
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }
}