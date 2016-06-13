package com.golfapp.test.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.golfapp.test.GcmConstants;
import com.golfapp.test.R;
import com.golfapp.test.datafiles.HotelData;
import com.golfapp.test.datafiles.ImageData;
import com.golfapp.test.utils.TinyDB;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Golakiya on 7/6/2015.
 */
public class HotelDetailActivity extends BaseActivity {

    private SharedPreferences sp;
    private Dialog m_dialog;
    private int hotelID;
    private HotelData selectedHotel;
    private TextView badgeCountTV;


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        overridePendingTransition(0, 0);
//        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
//                getResources().getDimensionPixelSize(R.dimen.badge_size),
//                getResources().getDimensionPixelSize(R.dimen.badge_size), Gravity.RIGHT);
//        badgeCountTV.setLayoutParams(params);
        String sid = GcmConstants.HOTEL + hotelID;
        int particularNewsBadgeCont = store.getInt(sid + "", 0);        // get Notification count for this hotel
        boolean isHotel = TinyDB.getInstance(this).getBoolean(sid + "", false);
        if (particularNewsBadgeCont > 0 && isHotel ) {
            badgeCountTV.setText(particularNewsBadgeCont + "");
            badgeCountTV.setVisibility(View.VISIBLE);
        } else {
            badgeCountTV.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hotel_detail);
        getSupportActionBar().hide();
        Button thecourse = (Button) findViewById(R.id.thecour);
        thecourse.setText(getString(R.string.htl_contact_pop_up_title));
        thecourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeetime();
            }
        });
        addToBack(this);
        badgeCountTV = (TextView) findViewById(R.id.badgeCountTV);
        Button fac = (Button) findViewById(R.id.faci);
        fac.setText(getString(R.string.htl_website_btn));
        TextView rates = (TextView) findViewById(R.id.ratess);
        rates.setText(getString(R.string.htl_package_btn));
        SliderLayout mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        Typeface tp = Typeface.createFromAsset(getAssets(), "fonts/B.ttf");
        Typeface lt = Typeface.createFromAsset(getAssets(), "fonts/LT.ttf");
        TextView des = (TextView) findViewById(R.id.desctext1);
        des.setTypeface(lt);
        thecourse.setTypeface(tp);
        fac.setTypeface(tp);
        rates.setTypeface(tp);
        hotelID = getIntent().getIntExtra("HotelID", 0);
        selectedHotel = Select.from(HotelData.class).where(Condition.prop("hotel_id").eq(hotelID)).first();
        selectedHotel.imageList = Select.from(ImageData.class).where(Condition.prop("hotel_id").eq(hotelID)).list();
        des.setText(selectedHotel.desc);
        des.setLinkTextColor(Color.parseColor("#94BC0D"));
        Linkify.addLinks(des, Linkify.WEB_URLS);
        TextView tit = (TextView) findViewById(R.id.title1);
        tit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tit.setText(selectedHotel.name);
        if (selectedHotel.imageList.size() <= 0) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
        } else if (selectedHotel.imageList.size() <= 1) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
            RoundedImageView iv = (RoundedImageView) findViewById(R.id.lvProDetail);
            RoundedImageView placeHolder = (RoundedImageView) findViewById(R.id.placeHolderImage);
            iv.setVisibility(View.VISIBLE);
            try {
                Picasso.with(this)
                        .load(selectedHotel.imageList.get(0).url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.drawable.a_place_holder_detail_page)
                        .error(R.drawable.a_place_holder_detail_page)
                        .into(iv, loadImage(iv, placeHolder, selectedHotel.imageList.get(0).url,R.drawable.a_place_holder_detail_page));
                //il.loadImage(selectedRoom.imageList.get(0).url, options, new Listener(iv));
            } catch (Exception e) {
                iv.setImageResource(R.drawable.a_place_holder_detail_page);
            }
        } else {
            for (int i = 0; i < selectedHotel.imageList.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                textSliderView.image(selectedHotel.imageList.get(i).url).setScaleType(BaseSliderView.ScaleType.CenterCrop).setOnSliderClickListener(null);
                textSliderView.bundle(new Bundle());
                //    textSliderView.getBundle().putString("extra", name);
                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.stopAutoCycle();
        }

        if (selectedHotel.email == null || selectedHotel.email.equals("") || selectedHotel.email.equals("null")) {
            if (selectedHotel.phone == null || selectedHotel.phone.equals("") || selectedHotel.phone.equals("null")) {
                thecourse.setBackgroundResource(R.drawable.btn_disable);
                thecourse.setClickable(false);
            }
        }

        if (selectedHotel.website != null) {
            if (selectedHotel.website.equals("") || selectedHotel.website.equals("null")) {
                fac.setEnabled(false);
                fac.setTextColor(Color.WHITE);
                fac.setBackgroundResource(R.drawable.btn_disable);
            }
        }
        String r = selectedHotel.package_count + "";
        Log.d("", "rate----" + r);
        if (r.compareToIgnoreCase("0") == 0) {
            rates.setBackgroundResource(R.drawable.btn_disable);
        } else {
            rates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isNetworkAvailable()) {
                        toast(getString(R.string.no_inet));
                    }
                    Intent it = new Intent(HotelDetailActivity.this, HotelPackageActivity.class);
                    it.putExtra("HotelID", selectedHotel.hotelID);
                    startActivity(it);
                }
            });
        }
        fac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                startActivity(new Intent(getApplicationContext(), BrowserActivity.class).
                        putExtra(BrowserActivity.URL, selectedHotel.website).
                        putExtra(BrowserActivity.AD_NAME, selectedHotel.name));
            }
        });


        setupActionbar();
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.courseDetailActionTitle)).setText(getString(R.string.htl_detail_nav_bar));
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

    String emm = "", ccc = "", dirr = "", c_web = "";

    public void showTeetime() {
        m_dialog = new Dialog(this);
        m_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        Window window = getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        // inflate and adjust layout
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom_dialog_hotel_detail, null);
        layout.setMinimumWidth((int) (displayRectangle.width() * 0.9f));
        layout.setMinimumHeight((int) (displayRectangle.height() * 0.2f));

        RelativeLayout email = (RelativeLayout) layout.findViewById(R.id.pros_mail);
        RelativeLayout phone = (RelativeLayout) layout.findViewById(R.id.pros_phone);
        RelativeLayout dir = (RelativeLayout) layout.findViewById(R.id.pros_dir);
        RelativeLayout web = (RelativeLayout) layout.findViewById(R.id.websiteRL);

        TextView tvEmail = (TextView) layout.findViewById(R.id.tvNewsDialogEmail);
        TextView tvPhone = (TextView) layout.findViewById(R.id.tvNewsDialogPhone);
        TextView tvdir = (TextView) layout.findViewById(R.id.tvNewsDialogdir);
        TextView tvCancel = (TextView) layout.findViewById(R.id.tvNewsDialogCancel);
        TextView tvWebsite = (TextView) layout.findViewById(R.id.tvNewsDialogWebsite);
        tvWebsite.setText(getString(R.string.htl_website_btn));
        tvEmail.setText(getString(R.string.cnt_email_btn));
        tvdir.setText(getString(R.string.cnt_direction_btn));
        tvPhone.setText(getString(R.string.cnt_phone_btn));
        tvCancel.setText(getString(R.string.cnt_cancel_btn));
        ((TextView) layout.findViewById(R.id.tvCDProDetailContact)).setText(getString(R.string.cnt_contact_pop_up_title));
        ((TextView) layout.findViewById(R.id.tvCDProDetailContact)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvEmail.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvPhone.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvCancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvdir.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvWebsite.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        emm = selectedHotel.email;


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = null;
                    try {
                        emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", emm, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    //  emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.ps_share_btn)));
                }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                    try {
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ccc));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
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
                if (dirr == null || dirr.equals("") || dirr.equals("null")) {
                } else {
                    uri = "http://maps.google.com/maps?q=" + selectedHotel.latitude + "," + selectedHotel.longitude;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(intent);
                }
            }
        });
        //m_llMain.setBackgroundResource(R.drawable.btn_style_border_roundcorner);
        c_web = selectedHotel.website;
        if (c_web.equals("") || c_web.equals("null")) {
            web.setBackgroundResource(R.drawable.btn_disable);
            web.setClickable(false);
            web.setVisibility(View.GONE);
        }

        if (emm == null || emm.equals("") || emm.equals("null")) {
            email.setVisibility(View.GONE);
            email.setBackgroundResource(R.drawable.btn_disable);
            email.setClickable(false);
            email.setVisibility(View.GONE);
        }
        ccc = selectedHotel.phone;
        if (ccc == null || ccc.equals("") || ccc.equals("null")) {
            phone.setVisibility(View.GONE);
            phone.setBackgroundResource(R.drawable.btn_disable);
            phone.setClickable(false);
            phone.setVisibility(View.GONE);
        }
        dirr = selectedHotel.latitude + "";
        if (dirr == null || dirr.equals("") || dirr.equals("null")) {
            dir.setVisibility(View.GONE);
            dir.setBackgroundResource(R.drawable.btn_disable);
            dir.setClickable(false);
            dir.setVisibility(View.GONE);
        }
        layout.findViewById(R.id.cancel_pros).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m_dialog.dismiss();
            }
        });
        m_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        m_dialog.setContentView(layout);
        m_dialog.show();
        m_dialog.setCanceledOnTouchOutside(true);
    }
}
