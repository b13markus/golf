package com.golfapp.test.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orm.query.Condition;
import com.orm.query.Select;

import com.golfapp.test.R;
import com.golfapp.test.datafiles.HotelData;


public class HotelWebsiteActivity extends BaseActivity {

    private String titt = "Website", url = "http://www.google.com";
    private int hotelID;
    private HotelData selectedHotel;

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
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_web);
        hotelID = getIntent().getIntExtra("HotelID", 0);
        selectedHotel = Select.from(HotelData.class).where(Condition.prop("hotel_id").eq(hotelID)).first();
        ProgressBar progressBar;
        progressBar = (ProgressBar) findViewById(R.id.prog);
        WebView urlWebView = (WebView) findViewById(R.id.web);
        urlWebView.setWebViewClient(new AppWebViewClients(progressBar));
        urlWebView.getSettings().setJavaScriptEnabled(true);
        urlWebView.loadUrl(selectedHotel.website);
        setupActionbar();
    }

    private void setupActionbar() {
        try {
            ((TextView) findViewById(R.id.courseDetailActionTitle)).setText(selectedHotel.name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((TextView) findViewById(R.id.courseDetailActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.courseDetailActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.courseDetailActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    public class AppWebViewClients extends WebViewClient {
        private ProgressBar progressBar;

        public AppWebViewClients(ProgressBar progressBar) {
            this.progressBar = progressBar;
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }
}
