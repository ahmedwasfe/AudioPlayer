package com.oceanforit.audioplayer;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.esotericsoftware.kryo.NotNull;
import com.google.android.material.bottomsheet.BottomSheetBehavior;


public class RetroBottomSheetBehavior extends BottomSheetBehavior {

    private static final String TAG = "RetroBottomSheetBehavior";

    private boolean allowDragging = true;

    public RetroBottomSheetBehavior() {
    }

    public RetroBottomSheetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setAllowDragging(boolean allowDragging) {
        this.allowDragging = allowDragging;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull CoordinatorLayout parent, @NonNull View child, @NonNull MotionEvent event) {
        if (!allowDragging) {
            return false;
        }
        return super.onInterceptTouchEvent(parent, child, event);
    }
}