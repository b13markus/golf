package com.golfapp.test.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

/**
 * Created by aman on 12/29/2015.
 */
public class MyListView extends ListView {

    private boolean isScrollable = true;


    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollable(boolean isScrollable) {
        this.isScrollable = isScrollable;
    }

    @Override
    protected void layoutChildren() {
        try {
            super.layoutChildren();
        } catch (Exception e) {

        }

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (!isScrollable)
                return true;
            else
                return super.dispatchTouchEvent(ev);
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            super.dispatchDraw(canvas);
        } catch (Exception e) {

        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        try {
            return super.onTouchEvent(ev);
        } catch (Exception e) {

        }
        return false;
    }


    @Override
    public View getRootView() {
        try {
            return super.getRootView();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
