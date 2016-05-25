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

import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.golfapp.test.GcmConstants;
import com.golfapp.test.R;
import com.golfapp.test.utils.Constants;
import com.golfapp.test.utils.TinyDB;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.orm.query.Condition;
import com.orm.query.Select;

/**
 * Created by Golakiya on 7/6/2015.
 */
public class RestaurantDetailActivity extends BaseActivity {

    private Dialog m_dialog;
    private SharedPreferences sp;
    private int restaurantID;
    private com.golfapp.test.datafiles.RestaurantData selectedRasturant;
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
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.badge_size),
                getResources().getDimensionPixelSize(R.dimen.badge_size), Gravity.RIGHT);
        badgeCountTV.setLayoutParams(params);
        String sid = GcmConstants.RESTAURANT + restaurantID;
        int particularNewsBadgeCont = store.getInt(sid + "", 0);        // get Notification count for this news
        boolean isRestaurant = TinyDB.getInstance(this).getBoolean(sid + "", false);
        if (particularNewsBadgeCont > 0 && isRestaurant) {
            badgeCountTV.setText(particularNewsBadgeCont + "");
            badgeCountTV.setVisibility(View.VISIBLE);
        } else {
            badgeCountTV.setVisibility(View.GONE);
        }
        if (store.getString(Constants.PACKAGE + sid) != null) {             // Is any notification for this page.
            store.setString(Constants.PACKAGE + sid, null);                 // Remove all the notification of package
            clearNotification(restaurantID, 0);                                      // clear notification on server
            int totalNewsBadgeCount = store.getInt(Constants.RESTAURANTS_PUSH_COUNT, 0);          // get the total notification badge count for Hotels
            store.setInt(Constants.RESTAURANTS_PUSH_COUNT, totalNewsBadgeCount - store.getInt(sid + "", 0));      // Subtract this hotel notification count from total notification count
            store.setInt(sid + "", 0);
            TinyDB.getInstance(this).putBoolean(sid, false);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rest_detail);
        getSupportActionBar().hide();
        badgeCountTV = (TextView) findViewById(R.id.badgeCountTV);
        Button thecourse = (Button) findViewById(R.id.thecour);
        thecourse.setText(getString(R.string.re_contact_btn));
        Button fac = (Button) findViewById(R.id.faci);
        fac.setText(getString(R.string.re_menu_btn));
        TextView rates = (TextView) findViewById(R.id.ratess);
        rates.setText(getString(R.string.re_suggestion_btn));
        SliderLayout mDemoSlider = (SliderLayout) findViewById(R.id.slider);
        Typeface tp = Typeface.createFromAsset(getAssets(), "fonts/B.ttf");
        Typeface lt = Typeface.createFromAsset(getAssets(), "fonts/LT.ttf");
        addToBack(this);
        TextView des = (TextView) findViewById(R.id.desctext1);
        des.setTypeface(lt);
        thecourse.setTypeface(tp);
        fac.setTypeface(tp);
        rates.setTypeface(tp);
        restaurantID = getIntent().getIntExtra("RestaurantID", 0);
        selectedRasturant = Select.from(com.golfapp.test.datafiles.RestaurantData.class).where(Condition.prop("restaurant_id").eq(restaurantID)).first();
        selectedRasturant.imageList = Select.from(com.golfapp.test.datafiles.ImageData.class).where(Condition.prop("restaurant_id").eq(restaurantID)).list();
        des.setText(selectedRasturant.descr);
        TextView tit = (TextView) findViewById(R.id.title1);
        tit.setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        tit.setText(selectedRasturant.name);
        if (selectedRasturant.imageList != null) {
            if (selectedRasturant.imageList.size() == 0) {
                //lvProDetail
                mDemoSlider.setVisibility(View.GONE);
                RoundedImageView iv = (RoundedImageView) findViewById(R.id.lvProDetail);
                iv.setVisibility(View.VISIBLE);
            } else if (selectedRasturant.imageList.size() == 1) {
                //lvProDetail
                mDemoSlider.setVisibility(View.GONE);
                RoundedImageView iv = (RoundedImageView) findViewById(R.id.lvProDetail);
                iv.setVisibility(View.VISIBLE);
                il.loadImage(selectedRasturant.imageList.get(0).url, options, new Listener(iv));
            } else {
                for (int i = 0; i < selectedRasturant.imageList.size(); i++) {
                    TextSliderView textSliderView = new TextSliderView(this);
                    // initialize a SliderLayout
                    textSliderView.image(selectedRasturant.imageList.get(i).url).setScaleType(BaseSliderView.ScaleType.CenterCrop).setOnSliderClickListener(null);
                    //add your extra information
                    textSliderView.bundle(new Bundle());
                    // textSliderView.getBundle().putString("extra", name);
                    mDemoSlider.addSlider(textSliderView);
                }
                mDemoSlider.stopAutoCycle();
            }
        }
        thecourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTeetime();
            }
        });

        String r = selectedRasturant.packageCount + "";
        Log.d("", "rate----" + r);
        if (r.compareToIgnoreCase("0") == 0) {
            rates.setBackgroundResource(R.drawable.btn_disable);
            rates.setTextColor(Color.WHITE);
        } else {
            rates.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isNetworkAvailable()) {
                        toast(getString(R.string.no_inet));
                        return;
                    }
                    Intent it = new Intent(RestaurantDetailActivity.this, com.golfapp.test.activities.RestaurantRatesActivity.class);
                    it.putExtra("RestaurantID", selectedRasturant.restaurantID);
                    startActivity(it);
                }
            });
        }
        String r1 = selectedRasturant.menuCount + "";
        Log.d("", "rate----" + r);
        if (r1.compareToIgnoreCase("0") == 0) {
            fac.setBackgroundResource(R.drawable.btn_disable);
            fac.setTextColor(Color.WHITE);
        } else {
            fac.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!isNetworkAvailable()) {
                        toast(getString(R.string.no_inet));
                        return;
                    }
                    Intent it = new Intent(RestaurantDetailActivity.this, RestaurantMenu.class);
                    it.putExtra("RestaurantID", selectedRasturant.restaurantID);
                    startActivity(it);
                }
            });
        }
        setupActionbar();
    }

    class Listener implements ImageLoadingListener {

        private RoundedImageView imageView;

        public Listener(RoundedImageView ivAdImage) {
            imageView = ivAdImage;
        }

        @Override
        public void onLoadingStarted(String imageUri, View view) {

        }

        @Override
        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

        }

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            imageView.setImageBitmap(loadedImage);
            /*Animation anim = AnimationUtils.loadAnimation(RestaurantDetailActivity.this, R.anim.fade);
            imageView.setAnimation(anim);
            anim.start();*/
        }

        @Override
        public void onLoadingCancelled(String imageUri, View view) {

        }
    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.courseDetailActionTitle)).setText(getString(R.string.re_detail_nav_bar));
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
                startActivity(new Intent(getApplicationContext(), com.golfapp.test.activities.MainActivity.class));
            }
        });
    }

    String emm = "", ccc = "", dirr = "";

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

        TextView tvEmail = (TextView) layout.findViewById(R.id.tvNewsDialogEmail);
        TextView tvPhone = (TextView) layout.findViewById(R.id.tvNewsDialogPhone);
        TextView tvdir = (TextView) layout.findViewById(R.id.tvNewsDialogdir);
        TextView tvCancel = (TextView) layout.findViewById(R.id.tvNewsDialogCancel);
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
        emm = selectedRasturant.email;
        if (emm.equals("") || emm.equals("null")) {
            email.setBackgroundResource(R.drawable.btn_disable);
            email.setClickable(false);
        }
        ccc = selectedRasturant.phone;
        if (ccc.equals("") || ccc.equals("null")) {
            phone.setBackgroundResource(R.drawable.btn_disable);
        }
        dirr = selectedRasturant.latitude + "";
        if (dirr.equals("") || dirr.equals("null")) {

            dir.setBackgroundResource(R.drawable.btn_disable);
        }
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = null;
                if (emm.equals("") || emm.equals("null")) {
                } else {
                    try {
                        emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", emm, null));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //  emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
                    //  emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                    startActivity(Intent.createChooser(emailIntent, getResources().getString(R.string.events_share_btn)));
                }
            }
        });
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = null;
                if (ccc.equals("") || ccc.equals("null")) {
                } else {
                    try {
                        intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + ccc));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                }
            }
        });

        dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = null;
                if (dirr.equals("") || dirr.equals("null")) {
                } else {
                    uri = "http://maps.google.com/maps?q=" + selectedRasturant.latitude + "," + selectedRasturant.longitude;
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
        m_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        m_dialog.setContentView(layout);
        m_dialog.show();
        m_dialog.setCanceledOnTouchOutside(true);
    }
}
