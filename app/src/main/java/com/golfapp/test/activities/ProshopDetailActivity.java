package com.golfapp.test.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bluejamesbond.text.DocumentView;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.golfapp.test.GcmConstants;
import com.golfapp.test.R;
import com.golfapp.test.datafiles.ImageData;
import com.golfapp.test.datafiles.ProshopData;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.TinyDB;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

/**
 * Created by Golakiya on 6/29/2015.
 */
public class ProshopDetailActivity extends BaseActivity {


    private TextView badgeCountTV;
    private Typeface tf;
    private SharedPreferences sp;
    private int proShopID;
    private ProshopData selectedProShop;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.badge_size),
                getResources().getDimensionPixelSize(R.dimen.badge_size), Gravity.RIGHT);
        badgeCountTV.setLayoutParams(params);
        overridePendingTransition(0, 0);
        String sid = GcmConstants.PROSHOP + proShopID;
        int particularNewsBadgeCont = store.getInt(sid + "", 0);        // get Notification count for this proshop
        boolean isProShop = TinyDB.getInstance(this).getBoolean(sid + "", false);
        if (particularNewsBadgeCont > 0 && isProShop) {
            badgeCountTV.setText(particularNewsBadgeCont + "");
            badgeCountTV.setVisibility(View.VISIBLE);
        } else {
            badgeCountTV.setVisibility(View.GONE);
        }
        if (store.getString(Constants.PACKAGE + sid) != null) {             // Is any notification for this page.
            store.setString(Constants.PACKAGE + sid, null);                 // Remove all the notification of package
            clearNotification(proShopID, 0);                                      // clear notification on server
            int totalNewsBadgeCount = store.getInt(Constants.PROSHOP_PUSH_COUNT, 0);          // get the total notification badge count for Hotels
            store.setInt(Constants.PROSHOP_PUSH_COUNT, totalNewsBadgeCount - store.getInt(sid + "", 0));      // Subtract this hotel notification count from total notification count
            store.setInt(sid + "", 0);
            TinyDB.getInstance(this).putBoolean(sid, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pros_detail);
        getSupportActionBar().hide();
        addToBack(this);
        proShopID = getIntent().getIntExtra("ProShopID", 0);
        selectedProShop = Select.from(ProshopData.class).where(Condition.prop("pro_shop_id").eq(proShopID)).first();
        selectedProShop.imageList = Select.from(ImageData.class).where(Condition.prop("pro_shop_id").eq(proShopID)).list();
        SliderLayout mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        DocumentView dv = (DocumentView) findViewById(R.id.desctext12);
        tf = Typeface.createFromAsset(getAssets(), "fonts/LT.ttf");
        TextView rates = (TextView) findViewById(R.id.rates);
        Button con = (Button) findViewById(R.id.contact);
        con.setText(getString(R.string.ps_contact_btn));
        badgeCountTV = (TextView) findViewById(R.id.badgeCountTV);
        rates.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        rates.setText(R.string.ps_special_offer_btn);
        con.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        TextView des = (TextView) findViewById(R.id.desctext1);
        des.setTypeface(tf);
        con.setOnClickListener(this);
        des.setText(selectedProShop.desc);
        dv.setText(selectedProShop.desc);
        TextView tit = (TextView) findViewById(R.id.title1);
        tit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tit.setText(selectedProShop.name);


        String r = selectedProShop.package_count + "";
        Log.d("", "rate----" + r);
        if (r.compareToIgnoreCase("0") == 0) {
            rates.setBackgroundResource(R.drawable.btn_disable);
        } else {
            rates.setOnClickListener(this);
        }

        if (selectedProShop.email == null || selectedProShop.email.equals("") || selectedProShop.email.equals("null")) {
            if (selectedProShop.phone == null || selectedProShop.phone.equals("") || selectedProShop.phone.equals("null")) {
                con.setBackgroundResource(R.drawable.btn_disable);
                con.setClickable(false);
            }
        }
        if (selectedProShop.imageList.size() <= 0) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
        } else if (selectedProShop.imageList.size() <= 1) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
            RoundedImageView iv = (RoundedImageView) findViewById(R.id.lvProDetail);
            RoundedImageView placeHolder = (RoundedImageView) findViewById(R.id.placeHolderImage);
            iv.setVisibility(View.VISIBLE);
            try {
                Picasso.with(this)
                        .load(selectedProShop.imageList.get(0).url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .config(Bitmap.Config.RGB_565)
                        .placeholder(R.drawable.a_place_holder_detail_page)
                        .error(R.drawable.a_place_holder_detail_page)
                        .into(iv, loadImage(iv, placeHolder, selectedProShop.imageList.get(0).url,R.drawable.a_place_holder_detail_page));
                //il.loadImage(selectedRoom.imageList.get(0).url, options, new Listener(iv));
            } catch (Exception e) {
                iv.setImageResource(R.drawable.a_place_holder_detail_page);
            }
        } else {
            for (int i = 0; i < selectedProShop.imageList.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                textSliderView.image(selectedProShop.imageList.get(i).url).setScaleType(BaseSliderView.ScaleType.CenterCrop).setOnSliderClickListener(null);
                textSliderView.bundle(new Bundle());
                //    textSliderView.getBundle().putString("extra", name);
                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.stopAutoCycle();
        }

        setupActionbar();

    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.prosDetailActionTitle)).setText(getString(R.string.ps_detail_nav_bar));
        ((TextView) findViewById(R.id.prosDetailActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.prosDetailActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((ImageView) findViewById(R.id.prosDetailActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    public void showCustomDialog() {
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
        TextView tvPhone = (TextView) layout.findViewById(R.id.tvNewsDialogPhone);
        TextView tvCancel = (TextView) layout.findViewById(R.id.tvNewsDialogCancel);
        ((TextView) layout.findViewById(R.id.tvCDProDetailContact)).setTypeface(tf);
        tvEmail.setText(getString(R.string.cnt_email_btn));
        tvPhone.setText(getString(R.string.cnt_phone_btn));
        tvCancel.setText(getString(R.string.cnt_cancel_btn));
        ((TextView) layout.findViewById(R.id.tvCDProDetailContact)).setText(getString(R.string.cnt_contact_pop_up_title));
        tvEmail.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvPhone.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tvCancel.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));

        if (selectedProShop.email == null || selectedProShop.email.equals("") || selectedProShop.email.equals("null")) {
            email.setBackgroundResource(R.drawable.btn_disable);
            email.setClickable(false);
        }
        if (selectedProShop.phone == null || selectedProShop.phone.equals("") || selectedProShop.phone.equals("null")) {
            phone.setBackgroundResource(R.drawable.btn_disable);
            phone.setClickable(false);
        }
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProShop.email == null || selectedProShop.email.equals("") || selectedProShop.email.equals("null")) {
                } else {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", selectedProShop.email, null));
                    //  emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    //  emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, (getString(R.string.events_share_btn))));
                }
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedProShop.phone == null || selectedProShop.phone.equals("") || selectedProShop.phone.equals("null")) {
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + selectedProShop.phone));
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

    private Dialog m_dialog; //Dialog instance.

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.rates:
                if (!isNetworkAvailable()) {
                    toast(getString(R.string.no_inet));
                }
                Intent it = new Intent(getApplicationContext(), ProshopRateOfferActivity.class);
                it.putExtra("ProShopID", selectedProShop.proShopID);
                startActivity(it);
                break;
            case R.id.contact:
                showCustomDialog();
                break;
        }
    }
}
