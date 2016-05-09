package com.golfapp.test.activities;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.golfapp.test.R;


/**
 * Created by anuj on 11/24/2015.
 */
public class BrowserActivity extends BaseActivity {
    public static final String URL = "URL";
    public static final String AD_NAME = "AD_NAME";

    private ProgressBar progressBar;
    private WebView webview;

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
        setContentView(R.layout.l_browser);
        getSupportActionBar().hide();
        TextView tvTitle = (TextView) findViewById(R.id.courseDetailActionTitle);
        tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(100);
        webview = (WebView) findViewById(R.id.webview);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        ((ImageView) findViewById(R.id.courseDetailActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((ImageView) findViewById(R.id.courseDetailActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }
        });

        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                progressBar.setProgress(newProgress);
            }
        });
        String adName = getIntent().getStringExtra(AD_NAME);
        String adUrl = getIntent().getStringExtra(URL).toLowerCase();
        if (!adUrl.contains("http")) {
            adUrl = "http://" + adUrl;
        }
        webview.loadUrl(adUrl);
        tvTitle.setText(adName);
    }

    @Override
    public void onBackPressed() {
        if (webview != null && webview.canGoBack()) {
            webview.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
