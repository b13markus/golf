package com.golfapp.test.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.golfapp.test.utils.Constants;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import com.golfapp.test.R;


public class Advertisement extends BaseActivity {
    JSONObject obj = null;
    ImageView img;
    private String url;
    private String name;
    private String image;

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
        setContentView(R.layout.l_ad_dialog);
        img = (ImageView) findViewById(R.id.adimage);
        try {
            obj = new JSONObject(store.getString(Constants.PROFILE_STRING));
            JSONObject advert = obj.getJSONObject("advertisemet");
            url = advert.getString("url");
            image = advert.getString("image");
            name = advert.getString("name");
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .cacheOnDisc(true)
                    .build();
            il.loadImage(image, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    img.setImageBitmap(loadedImage);
                    findViewById(R.id.close).setVisibility(View.VISIBLE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (obj != null) {
                    startActivity(new Intent(getApplicationContext(), AdvertisementActivity.class)
                            .putExtra(BrowserActivity.URL, url).putExtra(BrowserActivity.AD_NAME, name));
                    showAdvertisements = true;
                    finish();
                }
            }
        });
        findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdvertisements = true;
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        showAdvertisements = true;
        finish();
    }
}

