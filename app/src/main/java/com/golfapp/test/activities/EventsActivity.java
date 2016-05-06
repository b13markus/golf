package com.golfapp.test.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.golfapp.test.R;


/**
 * Created by aman on 11/24/2015.
 */
public class EventsActivity extends com.golfapp.test.activities.BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        addToStack(this);
        getSupportActionBar().hide();
        setupActionbar();
        ((Button) findViewById(R.id.evtUp)).setOnClickListener(this);
        ((Button) findViewById(R.id.evtUp)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((Button) findViewById(R.id.evtUp)).setText(getString(R.string.evt_upcoming_event_btn));
        ((Button) findViewById(R.id.evtPass)).setOnClickListener(this);
        ((Button) findViewById(R.id.evtPass)).setText(getString(R.string.evt_pastevent_btn));
        ((Button) findViewById(R.id.evtPass)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        showUpcomingEvents();
    }

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
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.evtUp: {
                ((Button) findViewById(R.id.evtPass)).setBackgroundResource(R.drawable.btn_disable);
                ((Button) findViewById(R.id.evtUp)).setBackgroundResource(R.drawable.btn_round);
                showUpcomingEvents();
                break;
            }
            case R.id.evtPass: {
                ((Button) findViewById(R.id.evtUp)).setBackgroundResource(R.drawable.btn_disable);
                ((Button) findViewById(R.id.evtPass)).setBackgroundResource(R.drawable.btn_round);
                showPastEvents();
                break;
            }
        }
    }


    @Override
    public void networkAvailable() {
        super.networkAvailable();
    }

    private void showUpcomingEvents() {
        getSupportFragmentManager().beginTransaction().replace(R.id.eventsContainer, new com.golfapp.test.fragments.UpcomingEventsFragment()).commitAllowingStateLoss();
    }

    private void showPastEvents() {
        getSupportFragmentManager().beginTransaction().replace(R.id.eventsContainer, new com.golfapp.test.fragments.PastEventsFragment()).commitAllowingStateLoss();

    }

    private void setupActionbar() {
        ((TextView) findViewById(R.id.eventActionTitle)).setText(R.string.evt_list_view_nav_bar);
        ((TextView) findViewById(R.id.eventActionTitle)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/B.ttf"));
        ((ImageView) findViewById(R.id.eventActionBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ((ImageView) findViewById(R.id.eventActionHome)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
