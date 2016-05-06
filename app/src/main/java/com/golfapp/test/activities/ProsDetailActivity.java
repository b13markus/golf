package com.golfapp.test.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.makeramen.roundedimageview.RoundedImageView;
import com.orm.query.Condition;
import com.orm.query.Select;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import com.golfapp.test.R;

/**
 * Created by Golakiya on 6/29/2015.
 */
public class ProsDetailActivity extends com.golfapp.test.activities.BaseActivity {

    SliderLayout mDemoSlider;
    int prosID = 0;
    Button con;
    TextView rates, badgeCountTV, des;
    Typeface tf;
    com.golfapp.test.utils.BadgeView bnews;
    private com.golfapp.test.datafiles.ProsData selectedPros;


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
        int particularNewsBadgeCont = store.getInt(prosID + "", 0);         // any notification for this pros
        if (particularNewsBadgeCont > 0) {
            badgeCountTV.setText(particularNewsBadgeCont + "");
            badgeCountTV.setVisibility(View.VISIBLE);
        } else {
            badgeCountTV.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pros_detail);
        getSupportActionBar().hide();
        addToStack(this);
        mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        tf = Typeface.createFromAsset(getAssets(), "fonts/LT.ttf");

        rates = (TextView) findViewById(R.id.rates);
        con = (Button) findViewById(R.id.contact);
        con.setText(getString(R.string.pro_contact_btn));
        badgeCountTV = (TextView) findViewById(R.id.badgeCountTV);
        rates.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        rates.setText(getString(R.string.pro_rate_offer_btn));
        con.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        des = (TextView) findViewById(R.id.desctext1);
        des.setTypeface(tf);
        con.setOnClickListener(this);
        prosID = getIntent().getIntExtra("ProsID", 0);

        selectedPros = Select.from(com.golfapp.test.datafiles.ProsData.class).where(Condition.prop("pros_id").eq(prosID)).first();
        selectedPros.imageList = Select.from(com.golfapp.test.datafiles.ImageData.class).where(Condition.prop("pros_id").eq(prosID)).list();
        TextView tit = (TextView) findViewById(R.id.title1);
        tit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tit.setText(selectedPros.name);
        des.setText(selectedPros.desc);
        if (selectedPros.package_count == 0) {
            rates.setBackgroundResource(R.drawable.btn_disable);
        } else {
            rates.setOnClickListener(this);
        }
        if (selectedPros.email == null || selectedPros.email.equals("") || selectedPros.email.equals("null")) {
            if (selectedPros.phone == null || selectedPros.phone.equals("") || selectedPros.phone.equals("null")) {
                con.setBackgroundResource(R.drawable.btn_disable);
                con.setClickable(false);
            }
        }
        bnews = new com.golfapp.test.utils.BadgeView(this);
        bnews.setTargetView(rates);
        bnews.setTextSize(getResources().getDimension(R.dimen.badge_textsize));
        // bnews.setBadgeCount(cn);

        if (selectedPros.imageList.size() <= 0) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
        } else if (selectedPros.imageList.size() <= 1) {
            mDemoSlider.setVisibility(View.GONE);
            findViewById(R.id.imageFrameFL).setVisibility(View.VISIBLE);
            RoundedImageView iv = (RoundedImageView) findViewById(R.id.lvProDetail);
            RoundedImageView placeHolder = (RoundedImageView) findViewById(R.id.placeHolderImage);
            iv.setVisibility(View.VISIBLE);
            try {
                Picasso.with(this)
                        .load(selectedPros.imageList.get(0).url)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.a_place_holder_detail_page)
                        .error(R.drawable.a_place_holder_detail_page)
                        .into(iv, loadImage(iv, placeHolder, selectedPros.imageList.get(0).url,R.drawable.a_place_holder_detail_page));
                //il.loadImage(selectedRoom.imageList.get(0).url, options, new Listener(iv));
            } catch (Exception e) {
                iv.setImageResource(R.drawable.a_place_holder_detail_page);
            }
        } else {
            for (int i = 0; i < selectedPros.imageList.size(); i++) {
                TextSliderView textSliderView = new TextSliderView(this);
                textSliderView.setScaleType(BaseSliderView.ScaleType.CenterCrop);
                textSliderView.image(selectedPros.imageList.get(i).url).setScaleType(BaseSliderView.ScaleType.CenterCrop).setOnSliderClickListener(null);
                textSliderView.bundle(new Bundle());
                //    textSliderView.getBundle().putString("extra", name);
                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.stopAutoCycle();
        }

        setupActionbar();
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.prosDetailActionTitle)).setText(getString(R.string.pro_detail_nav_bar));
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
                startActivity(new Intent(getApplicationContext(), com.golfapp.test.activities.MainActivity.class));
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

        if (selectedPros.email == null || selectedPros.email.equals("") || selectedPros.email.equals("null")) {
            email.setBackgroundResource(R.drawable.btn_disable);
            email.setClickable(false);
        }
        if (selectedPros.phone == null || selectedPros.phone.equals("") || selectedPros.phone.equals("null")) {
            phone.setBackgroundResource(R.drawable.btn_disable);
            phone.setClickable(false);
        }
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPros.email == null || selectedPros.email.equals("") || selectedPros.email.equals("null")) {
                } else {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", selectedPros.email, null));
                    //  emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    //  emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.events_share_btn)));
                }
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPros.phone == null || selectedPros.phone.equals("") || selectedPros.phone.equals("null")) {
                } else {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + selectedPros.phone));
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
                Intent it = new Intent(getApplicationContext(), com.golfapp.test.activities.ProDetailRateOffer.class);
                it.putExtra("ProsID", selectedPros.prosID);
                startActivity(it);
                break;
            case R.id.contact:
                showCustomDialog();
                break;
        }
    }
}
