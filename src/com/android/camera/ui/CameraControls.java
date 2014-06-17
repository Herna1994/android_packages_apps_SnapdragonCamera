/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera.ui;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.util.Log;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import java.util.ArrayList;
import android.graphics.drawable.AnimationDrawable;

import com.android.camera2.R;

public class CameraControls extends RotatableLayout {

    private static final String TAG = "CAM_Controls";

    private View mBackgroundView;
    private View mShutter;
    private View mSwitcher;
    private View mMenu;
    private View mFrontBackSwitcher;
    private View mFlashSwitcher;
    private View mHdrSwitcher;
    private View mIndicators;
    private View mPreview;
    private int mSize;
    private static final int wGrid = 5;
    private static final int hGrid = 7;

    AnimatorListener listener = new AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }

        @Override
        public void onAnimationEnd(Animator animation) {
            int rotation = getUnifiedRotation();
            switch (rotation) {
                case 0:
                    mFrontBackSwitcher.setY(mFrontBackSwitcher.getY() + mSize);
                    mFlashSwitcher.setY(mFlashSwitcher.getY() + mSize);
                    mHdrSwitcher.setY(mHdrSwitcher.getY() + mSize);

                    mSwitcher.setY(mSwitcher.getY() - mSize);
                    mShutter.setY(mShutter.getY() - mSize);
                    mMenu.setY(mMenu.getY() - mSize);
                    mIndicators.setY(mIndicators.getY() - mSize);
                    break;

                case 90:
                    mFrontBackSwitcher.setX(mFrontBackSwitcher.getX() + mSize);
                    mFlashSwitcher.setX(mFlashSwitcher.getX() + mSize);
                    mHdrSwitcher.setX(mHdrSwitcher.getX() + mSize);

                    mSwitcher.setX(mSwitcher.getX() - mSize);
                    mShutter.setX(mShutter.getX() - mSize);
                    mMenu.setX(mMenu.getX() - mSize);
                    mIndicators.setX(mIndicators.getX() - mSize);

                    break;
                case 180:

                    mFrontBackSwitcher.setY(mFrontBackSwitcher.getY() - mSize);
                    mFlashSwitcher.setY(mFlashSwitcher.getY() - mSize);
                    mHdrSwitcher.setY(mHdrSwitcher.getY() - mSize);

                    mSwitcher.setY(mSwitcher.getY() + mSize);
                    mShutter.setY(mShutter.getY() + mSize);
                    mMenu.setY(mMenu.getY() + mSize);
                    mIndicators.setY(mIndicators.getY() + mSize);

                    break;
                case 270:

                    mFrontBackSwitcher.setX(mFrontBackSwitcher.getX() - mSize);
                    mFlashSwitcher.setX(mFlashSwitcher.getX() - mSize);
                    mHdrSwitcher.setX(mHdrSwitcher.getX() - mSize);

                    mSwitcher.setX(mSwitcher.getX() + mSize);
                    mShutter.setX(mShutter.getX() + mSize);
                    mMenu.setX(mMenu.getX() + mSize);
                    mIndicators.setX(mIndicators.getX() + mSize);

                    break;
            }

            mFrontBackSwitcher.setVisibility(View.INVISIBLE);

            mFlashSwitcher.setVisibility(View.INVISIBLE);
            mHdrSwitcher.setVisibility(View.INVISIBLE);

            mSwitcher.setVisibility(View.INVISIBLE);
            mShutter.setVisibility(View.INVISIBLE);
            mMenu.setVisibility(View.INVISIBLE);
            mIndicators.setVisibility(View.INVISIBLE);
            mPreview.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }
    };

    public CameraControls(Context context, AttributeSet attrs) {
        super(context, attrs);
        setMeasureAllChildren(true);
    }

    public CameraControls(Context context) {
        super(context);
        setMeasureAllChildren(true);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        mBackgroundView = findViewById(R.id.blocker);
        mSwitcher = findViewById(R.id.camera_switcher);
        mShutter = findViewById(R.id.shutter_button);
        mFrontBackSwitcher = findViewById(R.id.front_back_switcher);
        mFlashSwitcher = findViewById(R.id.flash_switcher);
        mHdrSwitcher = findViewById(R.id.hdr_switcher);
        mMenu = findViewById(R.id.menu);
        mIndicators = findViewById(R.id.on_screen_indicators);
        mPreview = findViewById(R.id.preview_thumb);
    }

    @Override
    public void onLayout(boolean changed, int l, int t, int r, int b) {
        int orientation = getResources().getConfiguration().orientation;
        int size = getResources().getDimensionPixelSize(R.dimen.camera_controls_size);
        int rotation = getUnifiedRotation();
        adjustBackground();
        // As l,t,r,b are positions relative to parents, we need to convert them
        // to child's coordinates
        r = r - l;
        b = b - t;
        l = 0;
        t = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            v.layout(l, t, r, b);
        }
        Rect shutter = new Rect();
        topRight(mPreview, l, t, r, b);
        center(mShutter, l, t, r, b, orientation, rotation, shutter);
        mSize = Math.max(shutter.right - shutter.left, shutter.bottom - shutter.top);
        center(mBackgroundView, l, t, r, b, orientation, rotation, new Rect());
        mBackgroundView.setVisibility(View.GONE);
        toIndex(mSwitcher, r - l, b - t, rotation, 4, 6);
        toIndex(mMenu, r - l, b - t, rotation, 0, 6);
        toIndex(mIndicators, r - l, b - t, rotation, 0, 6);
        toIndex(mFrontBackSwitcher, r - l, b - t, rotation, 0, 0);
        toIndex(mFlashSwitcher, r - l, b - t, rotation, 2, 0);
        toIndex(mHdrSwitcher, r - l, b - t, rotation, 4, 0);
        View retake = findViewById(R.id.btn_retake);
        if (retake != null) {
            center(retake, shutter, rotation);
            View cancel = findViewById(R.id.btn_cancel);
            toLeft(cancel, shutter, rotation);
            View done = findViewById(R.id.btn_done);
            toRight(done, shutter, rotation);
        }
    }

    private void center(View v, int l, int t, int r, int b, int orientation, int rotation, Rect result) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
        int tw = lp.leftMargin + v.getMeasuredWidth() + lp.rightMargin;
        int th = lp.topMargin + v.getMeasuredHeight() + lp.bottomMargin;
        switch (rotation) {
        case 0:
            // phone portrait; controls bottom
            result.left = (r + l) / 2 - tw / 2 + lp.leftMargin;
            result.right = (r + l) / 2 + tw / 2 - lp.rightMargin;
            result.bottom = b - lp.bottomMargin;
            result.top = b - th + lp.topMargin;
            break;
        case 90:
            // phone landscape: controls right
            result.right = r - lp.rightMargin;
            result.left = r - tw + lp.leftMargin;
            result.top = (b + t) / 2 - th / 2 + lp.topMargin;
            result.bottom = (b + t) / 2 + th / 2 - lp.bottomMargin;
            break;
        case 180:
            // phone upside down: controls top
            result.left = (r + l) / 2 - tw / 2 + lp.leftMargin;
            result.right = (r + l) / 2 + tw / 2 - lp.rightMargin;
            result.top = t + lp.topMargin;
            result.bottom = t + th - lp.bottomMargin;
            break;
        case 270:
            // reverse landscape: controls left
            result.left = l + lp.leftMargin;
            result.right = l + tw - lp.rightMargin;
            result.top = (b + t) / 2 - th / 2 + lp.topMargin;
            result.bottom = (b + t) / 2 + th / 2 - lp.bottomMargin;
            break;
        }
        v.layout(result.left, result.top, result.right, result.bottom);
    }

    public void hideUI() {
        int rotation = getUnifiedRotation();
        mFrontBackSwitcher.animate().setListener(listener);

        int animeDuration = 300;
        switch (rotation) {
            case 0:
                mFrontBackSwitcher.animate().translationYBy(-mSize).setDuration(animeDuration);
                mFlashSwitcher.animate().translationYBy(-mSize).setDuration(animeDuration);
                mHdrSwitcher.animate().translationYBy(-mSize).setDuration(animeDuration);

                mSwitcher.animate().translationYBy(mSize).setDuration(animeDuration);
                mShutter.animate().translationYBy(mSize).setDuration(animeDuration);
                mMenu.animate().translationYBy(mSize).setDuration(animeDuration);
                mIndicators.animate().translationYBy(mSize).setDuration(animeDuration);
                break;
            case 90:
                mFrontBackSwitcher.animate().translationXBy(-mSize).setDuration(animeDuration);
                mFlashSwitcher.animate().translationXBy(-mSize).setDuration(animeDuration);
                mHdrSwitcher.animate().translationXBy(-mSize).setDuration(animeDuration);

                mSwitcher.animate().translationXBy(mSize).setDuration(animeDuration);
                mShutter.animate().translationXBy(mSize).setDuration(animeDuration);
                mMenu.animate().translationXBy(mSize).setDuration(animeDuration);
                mIndicators.animate().translationXBy(mSize).setDuration(animeDuration);
                break;
            case 180:
                mFrontBackSwitcher.animate().translationYBy(mSize).setDuration(animeDuration);
                mFlashSwitcher.animate().translationYBy(mSize).setDuration(animeDuration);
                mHdrSwitcher.animate().translationYBy(mSize).setDuration(animeDuration);

                mSwitcher.animate().translationYBy(-mSize).setDuration(animeDuration);
                mShutter.animate().translationYBy(-mSize).setDuration(animeDuration);
                mMenu.animate().translationYBy(-mSize).setDuration(animeDuration);
                mIndicators.animate().translationYBy(-mSize).setDuration(animeDuration);
                break;
            case 270:
                mFrontBackSwitcher.animate().translationXBy(mSize).setDuration(animeDuration);
                mFlashSwitcher.animate().translationXBy(mSize).setDuration(animeDuration);
                mHdrSwitcher.animate().translationXBy(mSize).setDuration(animeDuration);

                mSwitcher.animate().translationXBy(-mSize).setDuration(animeDuration);
                mShutter.animate().translationXBy(-mSize).setDuration(animeDuration);
                mMenu.animate().translationXBy(-mSize).setDuration(animeDuration);
                mIndicators.animate().translationXBy(-mSize).setDuration(animeDuration);
                break;
        }
    }

    public void showUI() {
        int rotation = getUnifiedRotation();
        mFrontBackSwitcher.setVisibility(View.VISIBLE);
        mFlashSwitcher.setVisibility(View.VISIBLE);
        mHdrSwitcher.setVisibility(View.VISIBLE);

        mSwitcher.setVisibility(View.VISIBLE);
        mShutter.setVisibility(View.VISIBLE);
        AnimationDrawable shutterAnim = (AnimationDrawable) mShutter.getBackground();
        if (shutterAnim != null)
            shutterAnim.stop();

        mMenu.setVisibility(View.VISIBLE);
        mIndicators.setVisibility(View.VISIBLE);

        int animeDuration = 300;
        mFrontBackSwitcher.animate().setListener(null);
        switch (rotation) {
            case 0:
                mFrontBackSwitcher.setY(mFrontBackSwitcher.getY() - mSize);
                mFlashSwitcher.setY(mFlashSwitcher.getY() - mSize);
                mHdrSwitcher.setY(mHdrSwitcher.getY() - mSize);

                mSwitcher.setY(mSwitcher.getY() + mSize);
                mShutter.setY(mShutter.getY() + mSize);
                mMenu.setY(mMenu.getY() + mSize);
                mIndicators.setY(mIndicators.getY() + mSize);

                mFrontBackSwitcher.animate().translationYBy(mSize).setDuration(animeDuration);
                mFlashSwitcher.animate().translationYBy(mSize).setDuration(animeDuration);
                mHdrSwitcher.animate().translationYBy(mSize).setDuration(animeDuration);

                mSwitcher.animate().translationYBy(-mSize).setDuration(animeDuration);
                mShutter.animate().translationYBy(-mSize).setDuration(animeDuration);
                mMenu.animate().translationYBy(-mSize).setDuration(animeDuration);
                mIndicators.animate().translationYBy(-mSize).setDuration(animeDuration);
                break;
            case 90:
                mFrontBackSwitcher.setX(mFrontBackSwitcher.getX() - mSize);
                mFlashSwitcher.setX(mFlashSwitcher.getX() - mSize);
                mHdrSwitcher.setX(mHdrSwitcher.getX() - mSize);

                mSwitcher.setX(mSwitcher.getX() + mSize);
                mShutter.setX(mShutter.getX() + mSize);
                mMenu.setX(mMenu.getX() + mSize);
                mIndicators.setX(mIndicators.getX() + mSize);

                mFrontBackSwitcher.animate().translationXBy(mSize).setDuration(animeDuration);
                mFlashSwitcher.animate().translationXBy(mSize).setDuration(animeDuration);
                mHdrSwitcher.animate().translationXBy(mSize).setDuration(animeDuration);

                mSwitcher.animate().translationXBy(-mSize).setDuration(animeDuration);
                mShutter.animate().translationXBy(-mSize).setDuration(animeDuration);
                mMenu.animate().translationXBy(-mSize).setDuration(animeDuration);
                mIndicators.animate().translationXBy(-mSize).setDuration(animeDuration);
                break;
            case 180:
                mFrontBackSwitcher.setY(mFrontBackSwitcher.getY() + mSize);
                mFlashSwitcher.setY(mFlashSwitcher.getY() + mSize);
                mHdrSwitcher.setY(mHdrSwitcher.getY() + mSize);

                mSwitcher.setY(mSwitcher.getY() - mSize);
                mShutter.setY(mShutter.getY() - mSize);
                mMenu.setY(mMenu.getY() - mSize);
                mIndicators.setY(mIndicators.getY() - mSize);

                mFrontBackSwitcher.animate().translationYBy(-mSize).setDuration(animeDuration);
                mFlashSwitcher.animate().translationYBy(-mSize).setDuration(animeDuration);
                mHdrSwitcher.animate().translationYBy(-mSize).setDuration(animeDuration);

                mSwitcher.animate().translationYBy(mSize).setDuration(animeDuration);
                mShutter.animate().translationYBy(mSize).setDuration(animeDuration);
                mMenu.animate().translationYBy(mSize).setDuration(animeDuration);
                mIndicators.animate().translationYBy(mSize).setDuration(animeDuration);
                break;
            case 270:
                mFrontBackSwitcher.setX(mFrontBackSwitcher.getX() + mSize);
                mFlashSwitcher.setX(mFlashSwitcher.getX() + mSize);
                mHdrSwitcher.setX(mHdrSwitcher.getX() + mSize);

                mSwitcher.setX(mSwitcher.getX() - mSize);
                mShutter.setX(mShutter.getX() - mSize);
                mMenu.setX(mMenu.getX() - mSize);
                mIndicators.setX(mIndicators.getX() - mSize);

                mFrontBackSwitcher.animate().translationXBy(-mSize).setDuration(animeDuration);
                mFlashSwitcher.animate().translationXBy(-mSize).setDuration(animeDuration);
                mHdrSwitcher.animate().translationXBy(-mSize).setDuration(animeDuration);

                mSwitcher.animate().translationXBy(mSize).setDuration(animeDuration);
                mShutter.animate().translationXBy(mSize).setDuration(animeDuration);
                mMenu.animate().translationXBy(mSize).setDuration(animeDuration);
                mIndicators.animate().translationXBy(mSize).setDuration(animeDuration);
                break;
        }
    }

    private void center(View v, Rect other, int rotation) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
        int tw = lp.leftMargin + v.getMeasuredWidth() + lp.rightMargin;
        int th = lp.topMargin + v.getMeasuredHeight() + lp.bottomMargin;
        int cx = (other.left + other.right) / 2;
        int cy = (other.top + other.bottom) / 2;
        v.layout(cx - tw / 2 + lp.leftMargin,
                cy - th / 2 + lp.topMargin,
                cx + tw / 2 - lp.rightMargin,
                cy + th / 2 - lp.bottomMargin);
    }

    private void toIndex(View v, int w, int h, int rotation, int index, int index2) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
        int tw = v.getMeasuredWidth();
        int th = v.getMeasuredHeight();
        int l = 0, r = 0, t = 0, b = 0;

        int wnumber = wGrid;
        int hnumber = hGrid;
        int windex = 0;
        int hindex = 0;
        switch (rotation) {
            case 0:
                // portrait, to left of anchor at bottom
                wnumber = wGrid;
                hnumber = hGrid;
                windex = index;
                hindex = index2;
                break;
            case 90:
                // phone landscape: below anchor on right
                wnumber = hGrid;
                hnumber = wGrid;
                windex = index2;
                hindex = hnumber - index - 1;
                break;
            case 180:
                // phone upside down: right of anchor at top
                wnumber = wGrid;
                hnumber = hGrid;
                windex = wnumber - index - 1;
                hindex = hnumber - index2 - 1;
                break;
            case 270:
                // reverse landscape: above anchor on left
                wnumber = hGrid;
                hnumber = wGrid;
                windex = wnumber - index2 - 1;
                hindex = index;
                break;
        }
        int boxh = h / hnumber;
        int boxw = w / wnumber;
        int cx = (2 * windex + 1) * boxw / 2;
        int cy = (2 * hindex + 1) * boxh / 2;

        l = cx - tw / 2;
        r = cx + tw / 2;
        t = cy - th / 2;
        b = cy + th / 2;
        v.layout(l, t, r, b);
    }

    private void toLeft(View v, Rect other, int rotation) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
        int tw = lp.leftMargin + v.getMeasuredWidth() + lp.rightMargin;
        int th = lp.topMargin + v.getMeasuredHeight() + lp.bottomMargin;
        int cx = (other.left + other.right) / 2;
        int cy = (other.top + other.bottom) / 2;
        int l = 0, r = 0, t = 0, b = 0;
        switch (rotation) {
        case 0:
            // portrait, to left of anchor at bottom
            l = other.left - tw + lp.leftMargin;
            r = other.left - lp.rightMargin;
            t = cy - th / 2 + lp.topMargin;
            b = cy + th / 2 - lp.bottomMargin;
            break;
        case 90:
            // phone landscape: below anchor on right
            l = cx - tw / 2 + lp.leftMargin;
            r = cx + tw / 2 - lp.rightMargin;
            t = other.bottom + lp.topMargin;
            b = other.bottom + th - lp.bottomMargin;
            break;
        case 180:
            // phone upside down: right of anchor at top
            l = other.right + lp.leftMargin;
            r = other.right + tw - lp.rightMargin;
            t = cy - th / 2 + lp.topMargin;
            b = cy + th / 2 - lp.bottomMargin;
            break;
        case 270:
            // reverse landscape: above anchor on left
            l = cx - tw / 2 + lp.leftMargin;
            r = cx + tw / 2 - lp.rightMargin;
            t = other.top - th + lp.topMargin;
            b = other.top - lp.bottomMargin;
            break;
        }
        v.layout(l, t, r, b);
    }

    private void toRight(View v, Rect other, int rotation) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) v.getLayoutParams();
        int tw = lp.leftMargin + v.getMeasuredWidth() + lp.rightMargin;
        int th = lp.topMargin + v.getMeasuredHeight() + lp.bottomMargin;
        int cx = (other.left + other.right) / 2;
        int cy = (other.top + other.bottom) / 2;
        int l = 0, r = 0, t = 0, b = 0;
        switch (rotation) {
        case 0:
            l = other.right + lp.leftMargin;
            r = other.right + tw - lp.rightMargin;
            t = cy - th / 2 + lp.topMargin;
            b = cy + th / 2 - lp.bottomMargin;
            break;
        case 90:
            l = cx - tw / 2 + lp.leftMargin;
            r = cx + tw / 2 - lp.rightMargin;
            t = other.top - th + lp.topMargin;
            b = other.top - lp.bottomMargin;
            break;
        case 180:
            l = other.left - tw + lp.leftMargin;
            r = other.left - lp.rightMargin;
            t = cy - th / 2 + lp.topMargin;
            b = cy + th / 2 - lp.bottomMargin;
            break;
        case 270:
            l = cx - tw / 2 + lp.leftMargin;
            r = cx + tw / 2 - lp.rightMargin;
            t = other.bottom + lp.topMargin;
            b = other.bottom + th - lp.bottomMargin;
            break;
        }
        v.layout(l, t, r, b);
    }

    private void topRight(View v, int l, int t, int r, int b) {
        // layout using the specific margins; the rotation code messes up the others
        int mt = getContext().getResources().getDimensionPixelSize(R.dimen.capture_margin_top);
        int mr = getContext().getResources().getDimensionPixelSize(R.dimen.capture_margin_right);
        v.layout(r - v.getMeasuredWidth() - mr, t + mt, r - mr, t + mt + v.getMeasuredHeight());
    }

    private void adjustBackground() {
        int rotation = getUnifiedRotation();
        // remove current drawable and reset rotation
        mBackgroundView.setBackgroundDrawable(null);
        mBackgroundView.setRotationX(0);
        mBackgroundView.setRotationY(0);
        // if the switcher background is top aligned we need to flip the background
        // drawable vertically; if left aligned, flip horizontally
        switch (rotation) {
            case 180:
                mBackgroundView.setRotationX(180);
                break;
            case 270:
                mBackgroundView.setRotationY(180);
                break;
            default:
                break;
        }
        mBackgroundView.setBackgroundResource(R.drawable.switcher_bg);
    }

}
