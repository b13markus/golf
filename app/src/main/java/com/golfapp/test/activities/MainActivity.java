package com.golfapp.test.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.golfapp.test.R;
import com.golfapp.test.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by aman on 11/21/2015.
 */
public class MainActivity extends BaseActivity {

    private static final int MY_SOCKET_TIMEOUT_MS = 100 * 30;
    private ImageView pros, event, proshop, courses, hotels, cont, news, resta, tee;
    private TextView restaurantBadgeTV;
    private TextView newsBadgeTV;
    private TextView hotelsBadgeTV;
    private TextView proShopBadgeTV;
    private TextView prosBadgeTV;
    private ArrayList<String> ons;
    private JSONObject obj;
    private Dialog m_dialog;
    private String emm = "", c_phone = "", cemm = "", cc_phone = "", cdir = "", clong = "", c_web = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(android.R.color.black));
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().hide();

        if (store.getBoolean("IsStartup", false)) {             // show add when app is killed and restarted
            store.setBoolean("IsStartup", false);
            showAdd();
        }
        if (store.getString(Constants.PROFILE_STRING) != null) {
            try {
                obj = new JSONObject(store.getString(Constants.PROFILE_STRING));
                JSONArray array = obj.names();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        initUI();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initUI() {
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                getResources().getDimensionPixelSize(R.dimen.badge_size),
//                getResources().getDimensionPixelSize(R.dimen.badge_size), Gravity.RIGHT);
        pros = (ImageView) findViewById(R.id.im_pros);
        tee = (ImageView) findViewById(R.id.im_tee);
        event = (ImageView) findViewById(R.id.im_event);
        proshop = (ImageView) findViewById(R.id.im_proshop);
        courses = (ImageView) findViewById(R.id.im_course);
        hotels = (ImageView) findViewById(R.id.im_hotel);
        cont = (ImageView) findViewById(R.id.im_cont);
        news = (ImageView) findViewById(R.id.im_news);
        resta = (ImageView) findViewById(R.id.im_rest);
        restaurantBadgeTV = (TextView) findViewById(R.id.restaurantBadgeTV);
        newsBadgeTV = (TextView) findViewById(R.id.newsBadgeTV);
        TextView contactUsBadgeTV = (TextView) findViewById(R.id.contactUsBadgeTV);
        hotelsBadgeTV = (TextView) findViewById(R.id.hotelsBadgeTV);
        TextView golfCourseBadgeTV = (TextView) findViewById(R.id.golfCourseBadgeTV);
        proShopBadgeTV = (TextView) findViewById(R.id.proShopBadgeTV);
        TextView tournamentsBadgeTV = (TextView) findViewById(R.id.tournamentsBadgeTV);
        TextView teeTimeBadgeTV = (TextView) findViewById(R.id.teeTimeBadgeTV);
        prosBadgeTV = (TextView) findViewById(R.id.prosBadgeTV);
//
//        restaurantBadgeTV.setLayoutParams(params);
//        newsBadgeTV.setLayoutParams(params);
//        contactUsBadgeTV.setLayoutParams(params);
//        hotelsBadgeTV.setLayoutParams(params);
//        proShopBadgeTV.setLayoutParams(params);
//        tournamentsBadgeTV.setLayoutParams(params);
//        teeTimeBadgeTV.setLayoutParams(params);
//        prosBadgeTV.setLayoutParams(params);
//        golfCourseBadgeTV.setLayoutParams(params);

        (findViewById(R.id.im_pros)).setOnClickListener(this);
        (findViewById(R.id.im_tee)).setOnClickListener(this);
        (findViewById(R.id.im_event)).setOnClickListener(this);
        (findViewById(R.id.im_proshop)).setOnClickListener(this);
        (findViewById(R.id.im_rest)).setOnClickListener(this);
        (findViewById(R.id.im_course)).setOnClickListener(this);
        (findViewById(R.id.im_hotel)).setOnClickListener(this);
        (findViewById(R.id.im_cont)).setOnClickListener(this);
        (findViewById(R.id.im_news)).setOnClickListener(this);
        /*(findViewById(R.id.llpros)).setOnClickListener(this);
        (findViewById(R.id.llNews)).setOnClickListener(this);
        (findViewById(R.id.llCourse)).setOnClickListener(this);
        (findViewById(R.id.llEvent)).setOnClickListener(this);
        (findViewById(R.id.llproshop)).setOnClickListener(this);
        (findViewById(R.id.lltee)).setOnClickListener(this);
        (findViewById(R.id.llhotel)).setOnClickListener(this);
        (findViewById(R.id.llrest)).setOnClickListener(this);
        (findViewById(R.id.llcont)).setOnClickListener(this);*/
        setButtons();
        ((TextView) findViewById(R.id.ttPros)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.ttTeeTime)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.ttEvents)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.ttProshop)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.ttCourse)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.ttHotels)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.ttContact)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.ttNews)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) findViewById(R.id.ttRest)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
    }

    private void setButtons() {
        if (store.getString(Constants.PROFILE_STRING) != null) {
            try {
                obj = new JSONObject(store.getString(Constants.PROFILE_STRING));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        ons = new ArrayList<>();
        try {
            if (obj != null) {
                JSONArray jr = obj.getJSONArray("buttons");
                for (int i = 0; i < jr.length(); i++) {
                    ons.add(jr.getString(i));
                }
                if (!ons.contains("hotel")) {
                    hotels.setImageResource(R.drawable.a_hotel_off);
                    hotels.setEnabled(false);
                    hotels.setClickable(false);
                } else {
                    hotels.setImageResource(R.drawable.a_hotel);
                    hotels.setEnabled(true);
                    hotels.setClickable(true);
                }
                if (!ons.contains("pros")) {
                    pros.setImageResource(R.drawable.a_pros_off);
                    pros.setEnabled(false);
                    pros.setClickable(false);
                } else {
                    pros.setImageResource(R.drawable.a_pros);
                    pros.setEnabled(true);
                    pros.setClickable(true);
                }
                if (!ons.contains("proshop")) {
                    proshop.setImageResource(R.drawable.a_proshop_off);
                    proshop.setEnabled(false);
                    proshop.setClickable(false);
                } else {
                    proshop.setImageResource(R.drawable.a_proshop);
                    proshop.setEnabled(true);
                    proshop.setClickable(true);
                }
                if (!ons.contains("restaurant")) {
                    resta.setImageResource(R.drawable.a_restaurant_off);
                    resta.setEnabled(false);
                    resta.setClickable(false);
                } else {
                    resta.setImageResource(R.drawable.a_restaurant);
                    resta.setEnabled(true);
                    resta.setClickable(true);
                }

                if (!ons.contains("news")) {
                    news.setImageResource(R.drawable.a_news_off);
                    news.setEnabled(false);
                    news.setClickable(false);
                } else {
                    news.setImageResource(R.drawable.a_news);
                    news.setEnabled(true);
                    news.setClickable(true);
                }
                if (!ons.contains("events")) {
                    event.setImageResource(R.drawable.a_events_off);
                    event.setEnabled(false);
                    event.setClickable(false);
                } else {
                    event.setImageResource(R.drawable.a_events);
                    event.setEnabled(true);
                    event.setClickable(true);
                }
                if (!ons.contains("courses")) {
                    courses.setImageResource(R.drawable.a_courses_off);
                    courses.setEnabled(false);
                    courses.setClickable(false);
                } else {
                    courses.setImageResource(R.drawable.a_courses);
                    courses.setEnabled(true);
                    courses.setClickable(true);
                }
                if (!ons.contains("teetime")) {
                    tee.setImageResource(R.drawable.a_tee_time_off);
                    tee.setEnabled(false);
                    tee.setClickable(false);
                } else {
                    tee.setImageResource(R.drawable.a_tee_time);
                    tee.setEnabled(true);
                    tee.setClickable(true);
                }
                if (!ons.contains("contact")) {
                    cont.setImageResource(R.drawable.a_contact_off);
                    cont.setEnabled(false);
                    cont.setClickable(false);
                } else {
                    cont.setImageResource(R.drawable.a_contact);
                    cont.setEnabled(true);
                    cont.setClickable(true);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.im_pros:
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                if (ons.contains("pros")) {
                    startActivity(new Intent(this, ProsActivity.class).putExtra("home", true));
                }
                break;
            case R.id.im_news:
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                if (ons.contains("news")) {
                    startActivity(new Intent(this, NewsActivity.class).putExtra("home", true));
                }
                break;
            case R.id.im_event:
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                if (ons.contains("events")) {
                    startActivity(new Intent(this, EventsActivity.class).putExtra("home", true));
                }
                break;
            case R.id.im_course:
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                if (ons.contains("courses")) {
                    startActivity(new Intent(this, CourseActivity.class).putExtra("home", true));
                }
                break;
            case R.id.im_proshop:
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                if (ons.contains("proshop")) {
                    startActivity(new Intent(this, ProshopActivity.class).putExtra("home", true));
                }
                break;
            case R.id.im_tee:
                if (ons.contains("teetime")) {
                    showTeetime();
                }
                break;
            case R.id.im_hotel:
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                if (ons.contains("hotel")) {
                    startActivity(new Intent(this, HotelsActivity.class).putExtra("home", true));
                }
                break;
            case R.id.im_rest:
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                if (ons.contains("restaurant")) {
                    startActivity(new Intent(this, RestaurantActivity.class).putExtra("home", true));
                }
                break;
            case R.id.im_cont:
                if (ons.contains("contact")) {
                    showcont();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        setBadgeCount();
        if (isNetworkAvailable()) {
            getProfile();
        } else {
            toast(getString(R.string.no_inet));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    private void getProfile() {
        String urlNews = Constants.urlAdvertising + "?client=" + Constants.clientId + "&sectoken=" + Constants.md5() + "&language=" + Constants.getLanguage();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlNews, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        store.setString(Constants.PROFILE_STRING, jsonObject.toString());
                        setButtons();
                    }
                }, this);

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        applicationInstance.addToRequestQueue(jsonObjReq, "Profile");
    }


    public void setBadgeCount() {
        int hotelBadgeCount = store.getInt(Constants.HOTEL_PUSH_COUNT, 0);
        if (hotelBadgeCount > 0) {
            hotelsBadgeTV.setText(hotelBadgeCount + "");
            hotelsBadgeTV.setVisibility(View.VISIBLE);
        } else
            hotelsBadgeTV.setVisibility(View.GONE);

        int restaurantBadgeCount = store.getInt(Constants.RESTAURANTS_PUSH_COUNT, 0);
        if (restaurantBadgeCount > 0) {
            restaurantBadgeTV.setText(restaurantBadgeCount + "");
            restaurantBadgeTV.setVisibility(View.VISIBLE);
        } else
            restaurantBadgeTV.setVisibility(View.GONE);


        int prosBadgeCount = store.getInt(Constants.PROS_PUSH_COUNT, 0);
        if (prosBadgeCount > 0) {
            prosBadgeTV.setText(prosBadgeCount + "");
            prosBadgeTV.setVisibility(View.VISIBLE);
        } else
            prosBadgeTV.setVisibility(View.GONE);

        int proShopBadgeCount = store.getInt(Constants.PROSHOP_PUSH_COUNT, 0);
        if (proShopBadgeCount > 0) {
            proShopBadgeTV.setText(proShopBadgeCount + "");
            proShopBadgeTV.setVisibility(View.VISIBLE);
        } else
            proShopBadgeTV.setVisibility(View.GONE);


        int newsBadgeCount = store.getInt(Constants.NEWS_PUSH_COUNT, 0);
        if (newsBadgeCount > 0) {
            newsBadgeTV.setText(newsBadgeCount + "");
            newsBadgeTV.setVisibility(View.VISIBLE);
        } else
            newsBadgeTV.setVisibility(View.GONE);


    }


    public void showTeetime() {
        m_dialog = new Dialog(this);
        m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        // inflate and adjust layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_dialog_pros_detail, null);
        layout.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int) (displayRectangle.height() * 0.2f));

        RelativeLayout email = (RelativeLayout) layout.findViewById(R.id.pros_mail);
        RelativeLayout phone = (RelativeLayout) layout.findViewById(R.id.pros_phone);

        TextView tvEmail = (TextView) layout.findViewById(R.id.tvNewsDialogEmail);
        tvEmail.setText(getString(R.string.email_button_btn));
        TextView tvPhone = (TextView) layout.findViewById(R.id.tvNewsDialogPhone);
        tvPhone.setText(getString(R.string.tt_phone_btn));
        TextView tvCancel = (TextView) layout.findViewById(R.id.tvNewsDialogCancel);
        tvCancel.setText(getString(R.string.tt_cancel_btn));
        ((TextView) layout.findViewById(R.id.tvCDProDetailContact)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((TextView) layout.findViewById(R.id.tvCDProDetailContact)).setText(getString(R.string.tt_book_tee_pop_up_title));
        tvEmail.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvPhone.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvCancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));


        try {
            JSONObject prof = obj.getJSONObject("profile");
            emm = prof.getString("email");

            if (emm.equals("") || emm.equals("null")) {
                email.setBackgroundResource(R.drawable.btn_disable);
                email.setClickable(false);
            }
            c_phone = prof.getString("phone");
            Log.d("", "phone--" + c_phone);
            if (c_phone.equals("") || c_phone.equals("null")) {
                phone.setBackgroundResource(R.drawable.btn_disable);
                phone.setClickable(false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emm.equals("") || emm.equals("null")) {
                } else {
                    Intent emailIntent = null;
                    try {
                        emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", emm, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.tt_book_tee_pop_up_title));
                    //  emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, "Send email..."));
                }
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (c_phone.equals("") || c_phone.equals("null")) {
                } else {
                    Intent intent = null;
                    try {
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + c_phone));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            }
        });
        //m_llMain.setBackgroundResource(R.drawable.btn_style_border_roundcorner);

        layout.findViewById(R.id.cancel_pros).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_dialog.dismiss();
            }
        });
        m_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        m_dialog.setContentView(layout);
        m_dialog.show();
        m_dialog.setCanceledOnTouchOutside(true);
    }

    public void showcont() {
        m_dialog = new Dialog(this);
        m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        // inflate and adjust layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.home_custom_dialog_hotel_detail, null);
        layout.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int) (displayRectangle.height() * 0.2f));

        RelativeLayout email = (RelativeLayout) layout.findViewById(R.id.pros_mail);
        RelativeLayout phone = (RelativeLayout) layout.findViewById(R.id.pros_phone);
        RelativeLayout dir = (RelativeLayout) layout.findViewById(R.id.pros_dir);
        RelativeLayout web = (RelativeLayout) layout.findViewById(R.id.websiteRL);

        TextView tvEmail = (TextView) layout.findViewById(R.id.tvNewsDialogEmail);
        tvEmail.setText(getString(R.string.cnt_email_btn));
        TextView tvPhone = (TextView) layout.findViewById(R.id.tvNewsDialogPhone);
        tvPhone.setText(getString(R.string.cnt_phone_btn));
        TextView tvdir = (TextView) layout.findViewById(R.id.tvNewsDialogdir);
        tvdir.setText(getString(R.string.cnt_direction_btn));
        TextView tvCancel = (TextView) layout.findViewById(R.id.tvNewsDialogCancel);
        tvCancel.setText(getString(R.string.cnt_cancel_btn));
        TextView tvWebsite = (TextView) layout.findViewById(R.id.tvNewsDialogWebsite);
        tvWebsite.setText(getString(R.string.htl_website_btn));
        ((TextView) layout.findViewById(R.id.tvCDProDetailContact)).setText(getString(R.string.cnt_contact_pop_up_title));
        ((TextView) layout.findViewById(R.id.tvCDProDetailContact)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvEmail.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvPhone.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvCancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvWebsite.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvdir.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));

        JSONObject prof = null;
        try {
            prof = obj.getJSONObject("profile");
            cemm = prof.getString("email");

            if (cemm.equals("") || cemm.equals("null")) {
                email.setBackgroundResource(R.drawable.btn_disable);
                email.setClickable(false);
                email.setVisibility(View.GONE);
            }
            cc_phone = prof.getString("phone");
            if (cc_phone.equals("") || cc_phone.equals("null")) {
                phone.setBackgroundResource(R.drawable.btn_disable);
                phone.setClickable(false);
                phone.setVisibility(View.GONE);
            }
            c_web = prof.getString("website");
            if (c_web.equals("") || c_web.equals("null")) {
                web.setBackgroundResource(R.drawable.btn_disable);
                web.setClickable(false);
                web.setVisibility(View.GONE);
            }
            cdir = prof.getString("latitude");
            clong = prof.getString("longitude");
            if (cdir.equals("") || cdir.equals("null")) {
                dir.setBackgroundResource(R.drawable.btn_disable);
                dir.setClickable(false);
                dir.setVisibility(View.GONE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cemm.equals("") || cemm.equals("null")) {
                } else {
                    Intent emailIntent = null;
                    try {
                        emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", cemm, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.events_share_btn)));
                }
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (cc_phone.equals("") || cc_phone.equals("null")) {
                } else {
                    try {
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + cc_phone));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String uri = null;
                if (c_web.equals("") || c_web.equals("null")) {
                } else {
                    try {
                        uri = c_web;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            }
        });

        dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = null;

                if (cdir.equals("") || cdir.equals("null")) {
                } else {
                    try {
                        uri = "http://maps.google.com/maps?q=" + cdir + "," + clong;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            }
        });
        //m_llMain.setBackgroundResource(R.drawable.btn_style_border_roundcorner);

        layout.findViewById(R.id.cancel_pros).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_dialog.dismiss();
            }
        });
        m_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        m_dialog.setContentView(layout);
        m_dialog.show();
        m_dialog.setCanceledOnTouchOutside(true);
    }
}
