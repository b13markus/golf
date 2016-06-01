package com.golfapp.test.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bluejamesbond.text.DocumentView;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.golfapp.test.GcmConstants;
import com.golfapp.test.R;
import com.golfapp.test.datafiles.ImageData;
import com.golfapp.test.datafiles.NewsData;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.TinyDB;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Golakiya on 6/30/2015.
 */
public class NewsDetailActivity extends BaseActivity {

    private static final int MY_SOCKET_TIMEOUT_MS = 100 * 30;
    private TextView title, stit, sdate, des;
    private SliderLayout mDemoSlider;
    private String urlNe = "";
    private DocumentView dv;
    private SharedPreferences sp;
    private int newsID;
    private NewsData selectedNews;

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
        String sid = GcmConstants.NEWS + newsID;
        if (store.getString(Constants.PACKAGE + sid) != null) {             // Is any notification for this page.
            store.setString(Constants.PACKAGE + sid, null);                 // Remove all the notification of package
            clearNotification(newsID, Integer.parseInt(Constants.clientId));                                      // clear notification on server
            int totalNewsBadgeCount = store.getInt(Constants.NEWS_PUSH_COUNT, 0);          // get the total notification badge count for Hotels
            store.setInt(Constants.NEWS_PUSH_COUNT, totalNewsBadgeCount - store.getInt(sid + "", 0));      // Subtract this hotel notification count from total notification count
            store.setInt(sid + "", 0);
            TinyDB.getInstance(this).putBoolean(sid, false);
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
        setContentView(R.layout.activity_news_detail);
        getSupportActionBar().hide();
        addToBack(this);
        newsID = getIntent().getIntExtra("NewsID", 0);
        int particularNewsBadgeCont = store.getInt(newsID + "", 0);
        if (particularNewsBadgeCont > 0) {
            store.setInt(newsID + "", 0);
            clearNotification(newsID, Integer.parseInt(Constants.clientId));
            int totalNewsBadgeCount = store.getInt(Constants.NEWS_PUSH_COUNT, 0);
            store.setInt(Constants.NEWS_PUSH_COUNT, totalNewsBadgeCount - particularNewsBadgeCont);
        }
        selectedNews = Select.from(NewsData.class).where(Condition.prop("news_id").eq(newsID)).first();

        title = (TextView) findViewById(R.id.tit1);//ivNewsImage
        stit = (TextView) findViewById(R.id.stit1);
        sdate = (TextView) findViewById(R.id.date1);

        // desc = (TextView) findViewById(R.id.desc1);
        dv = (DocumentView) findViewById(R.id.desctext13);
        Button btn = (Button) findViewById(R.id.btnShareOnNews);
        btn.setText(getString(R.string.nws_share_btn));

        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/LT.ttf");
        Typeface tfb = Typeface.createFromAsset(getAssets(), "fonts/B.ttf");
        Typeface tfi = Typeface.createFromAsset(getAssets(), "fonts/BI.ttf");
        des = (TextView) findViewById(R.id.desctext1);
        des.setTypeface(tf);
        Linkify.addLinks(des, Linkify.WEB_URLS);
        des.setLinkTextColor(Color.parseColor("#94BC0D"));
        title.setTypeface(tfb);
        stit.setTypeface(tfi);
        sdate.setTypeface(tf);
        //  desc.setTypeface(tf);
        btn.setTypeface(tfb);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        btn.setOnClickListener(this);
        if (selectedNews != null) {
            selectedNews.imageList = Select.from(ImageData.class).where(Condition.prop("news_id").eq(newsID)).list();
            setd(null);
        } else {
            if (isNetworkAvailable()) {
                getNewsData();
            }
        }
    }

    private void getNewsData() {
        String url = Constants.urlNewsDetail + "/" + newsID + "?client=" + Constants.clientId + "&sectoken=" + Constants.md5()
                + "&language=" + Constants.getLanguage();
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                url, null,
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
        try {
            if (jsonObject.getInt("success") == 1) {
                JSONObject obj = jsonObject.getJSONArray("news").getJSONObject(0);
                int newsID = obj.getInt("id");
                String title = obj.getString("title");
                String subtitle = obj.getString("subtitle");
                String descr = obj.getString("descr");
                String pubdate = obj.getString("pubdate");
                String updated = obj.getString("updated");
                selectedNews = Select.from(NewsData.class).where(Condition.prop("news_id").eq(newsID)).first();
                if (selectedNews == null) {
                    selectedNews = new NewsData(newsID, title, subtitle, descr, pubdate, updated);
                    selectedNews.save();
                } else {
                    selectedNews.title = title;
                    selectedNews.desc = descr;
                    selectedNews.pubDate = pubdate;
                    selectedNews.updated = updated;
                    selectedNews.subtitle = subtitle;
                    selectedNews.save();
                }
                JSONArray imagesArray = obj.getJSONArray("images");
                for (int b = 0; b < imagesArray.length(); b++) {
                    JSONObject imageObj = imagesArray.getJSONObject(b);
                    String Imagename = imageObj.getString("name");
                    String url = imageObj.getString("url");
                    ImageData.deleteAll(ImageData.class, "news_id = ? and name = ? and url = ? ", newsID + "", Imagename, url);
                    ImageData image = new ImageData(0, newsID, 0, 0, 0, 0, Imagename, url);
                    image.save();
                    selectedNews.imageList.add(image);
                }
                setd(null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                toast("Clicked");
            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }


    public void setd(String objjj) {
        title.setText(selectedNews.title);
        if (selectedNews.subtitle != null)
            if (selectedNews.subtitle.isEmpty())
                stit.setVisibility(View.GONE);
        stit.setText(selectedNews.subtitle);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date convertedDate = new Date();
        try {
            convertedDate = format.parse(selectedNews.pubDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        sdate.setText(outputFormat.format(convertedDate));
        // desc.setText(obj.getString("description"));
        dv.setText(selectedNews.desc);
        des.setText(selectedNews.desc);
        Linkify.addLinks(des, Linkify.WEB_URLS);
        des.setLinkTextColor(Color.parseColor("#94BC0D"));
        //setTextViewHTML(des, selectedNews.desc);
        if (selectedNews.imageList.size() <= 0) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
        } else if (selectedNews.imageList.size() <= 1) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
            RoundedImageView iv = (RoundedImageView) findViewById(R.id.lvProDetail);
            RoundedImageView placeHolder = (RoundedImageView) findViewById(R.id.placeHolderImage);
            iv.setVisibility(View.VISIBLE);
            try {
                Picasso.with(this)
                        .load(selectedNews.imageList.get(0).url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.drawable.a_place_holder_detail_page)
                        .error(R.drawable.a_place_holder_detail_page)
                        .into(iv, loadImage(iv, placeHolder, selectedNews.imageList.get(0).url,R.drawable.a_place_holder_detail_page));
                //il.loadImage(selectedRoom.imageList.get(0).url, options, new Listener(iv));
            } catch (Exception e) {
                iv.setImageResource(R.drawable.a_place_holder_detail_page);
            }
        } else {
            for (int i = 0; i < selectedNews.imageList.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                textSliderView.image(selectedNews.imageList.get(i).url).setScaleType(BaseSliderView.ScaleType.CenterCrop).setOnSliderClickListener(null);
                textSliderView.bundle(new Bundle());
                //    textSliderView.getBundle().putString("extra", name);
                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.stopAutoCycle();
        }

        setupActionbar();
    }

    @Override
    public void onClick(View v) {
        String body = title.getText() + "\n" + sdate.getText() + "\n" + des.getText();
        switch (v.getId()) {
            case R.id.btnShareOnNews:
                appShare(this, body);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getBooleanExtra("Push", false)) {
            startActivity(new Intent(this, NewsActivity.class).putExtra("Push", true));
            finish();
        } else {
            finish();
        }
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.newsDetailActionTitle)).setText(getString(R.string.nws_detail_nav_bar));
        ((TextView) findViewById(R.id.newsDetailActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.newsDetailActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ((ImageView) findViewById(R.id.newsDetailActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }

}
