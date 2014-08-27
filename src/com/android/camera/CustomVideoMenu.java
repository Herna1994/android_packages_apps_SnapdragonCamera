/*
 * Copyright (c) 2014, The Linux Foundation. All rights reserved.
 * Not a Contribution.
 *
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.camera;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.widget.ListView;

import com.android.camera.ui.ListSubMenu;
import com.android.camera.ui.ListMenu;
import com.android.camera.ui.TimeIntervalPopup;
import com.android.camera.ui.RotateImageView;
import com.android.camera2.R;

public class CustomVideoMenu extends MenuController
        implements ListMenu.Listener,
        ListSubMenu.Listener,
        TimeIntervalPopup.Listener {

    private static String TAG = "CustomVideoMenu";

    private VideoUI mUI;
    private String[] mOtherKeys1;
    private String[] mOtherKeys2;

    private ListMenu mListMenu;
    private ListSubMenu mListSubMenu;

    private static final int POPUP_NONE = 0;
    private static final int POPUP_FIRST_LEVEL = 1;
    private static final int POPUP_SECOND_LEVEL = 2;
    private static final int POPUP_IN_ANIMATION = 3;

    private RotateImageView mFrontBackSwitcher;
    private int mPopupStatus;
    private CameraActivity mActivity;
    private static final int ANIMATION_DURATION = 300;

    public CustomVideoMenu(CameraActivity activity, VideoUI ui) {
        super(activity);
        mUI = ui;
        mActivity = activity;
        mFrontBackSwitcher = (RotateImageView) ui.getRootView().findViewById(
                R.id.front_back_switcher);
    }

    public void initialize(PreferenceGroup group) {
        super.initialize(group);
        mListMenu = null;
        mListSubMenu = null;
        mPopupStatus = POPUP_NONE;
        // settings popup
        mOtherKeys1 = new String[] {
                CameraSettings.KEY_VIDEO_QUALITY,
                CameraSettings.KEY_VIDEO_DURATION,
                CameraSettings.KEY_RECORD_LOCATION,
                CameraSettings.KEY_CAMERA_SAVEPATH,
                CameraSettings.KEY_COLOR_EFFECT,
                CameraSettings.KEY_WHITE_BALANCE,
                CameraSettings.KEY_VIDEO_HIGH_FRAME_RATE,
                CameraSettings.KEY_VIDEOCAMERA_FLASH_MODE
        };
        mOtherKeys2 = new String[] {
                CameraSettings.KEY_VIDEO_QUALITY,
                CameraSettings.KEY_VIDEO_DURATION,
                CameraSettings.KEY_RECORD_LOCATION,
                CameraSettings.KEY_CAMERA_SAVEPATH,
                CameraSettings.KEY_COLOR_EFFECT,
                CameraSettings.KEY_WHITE_BALANCE,
                CameraSettings.KEY_VIDEO_HIGH_FRAME_RATE,
                CameraSettings.KEY_VIDEOCAMERA_FLASH_MODE,
                CameraSettings.KEY_DIS,
                CameraSettings.KEY_VIDEO_EFFECT,
                CameraSettings.KEY_VIDEO_TIME_LAPSE_FRAME_INTERVAL,
                CameraSettings.KEY_VIDEO_ENCODER,
                CameraSettings.KEY_AUDIO_ENCODER,
                CameraSettings.KEY_VIDEO_HDR,
                CameraSettings.KEY_POWER_MODE
        };
        mFrontBackSwitcher.setVisibility(View.INVISIBLE);
        initSwitchItem(CameraSettings.KEY_CAMERA_ID, mFrontBackSwitcher);
    }

    public boolean handleBackKey() {
        if (mPopupStatus == POPUP_NONE)
            return false;
        if (mPopupStatus == POPUP_FIRST_LEVEL) {
            animateSlideOut(mListMenu, 1);
        } else if (mPopupStatus == POPUP_SECOND_LEVEL) {
            animateFadeOut(mListSubMenu, 2);
            ((ListMenu) mListMenu).resetHighlight();
        }
        return true;
    }

    public void tryToCloseSubList() {
        if (mListMenu != null)
            ((ListMenu) mListMenu).resetHighlight();

        if (mPopupStatus == POPUP_SECOND_LEVEL) {
            mUI.dismissLevel2();
            mPopupStatus = POPUP_FIRST_LEVEL;
        }
    }

    private void animateFadeOut(final ListView v, final int level) {
        if (v == null || mPopupStatus == POPUP_IN_ANIMATION)
            return;
        mPopupStatus = POPUP_IN_ANIMATION;

        ViewPropertyAnimator vp = v.animate();
        vp.alpha(0f).setDuration(ANIMATION_DURATION);
        vp.setListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (level == 1) {
                    mUI.dismissLevel1();
                    initializePopup();
                    mPopupStatus = POPUP_NONE;
                    mUI.cleanupListview();
                }
                else if (level == 2) {
                    mUI.dismissLevel2();
                    mPopupStatus = POPUP_FIRST_LEVEL;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (level == 1) {
                    mUI.dismissLevel1();
                    initializePopup();
                    mPopupStatus = POPUP_NONE;
                    mUI.cleanupListview();
                }
                else if (level == 2) {
                    mUI.dismissLevel2();
                    mPopupStatus = POPUP_FIRST_LEVEL;
                }

            }
        });
        vp.start();
    }

    private void animateSlideOut(final ListView v, final int level) {
        if (v == null || mPopupStatus == POPUP_IN_ANIMATION)
            return;
        mPopupStatus = POPUP_IN_ANIMATION;

        ViewPropertyAnimator vp = v.animate();
        vp.translationX(v.getX() - v.getWidth()).setDuration(ANIMATION_DURATION);
        vp.setListener(new AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (level == 1) {
                    mUI.dismissLevel1();
                    initializePopup();
                    mPopupStatus = POPUP_NONE;
                    mUI.cleanupListview();
                }
                else if (level == 2) {
                    mUI.dismissLevel2();
                    mPopupStatus = POPUP_FIRST_LEVEL;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                if (level == 1) {
                    mUI.dismissLevel1();
                    initializePopup();
                    mPopupStatus = POPUP_NONE;
                    mUI.cleanupListview();
                }
                else if (level == 2) {
                    mUI.dismissLevel2();
                    mPopupStatus = POPUP_FIRST_LEVEL;
                }

            }
        });
        vp.start();
    }

    public void animateFadeIn(final ListView v) {
        ViewPropertyAnimator vp = v.animate();
        vp.alpha(0.85f).setDuration(ANIMATION_DURATION);
        vp.start();
    }

    public void animateSlideIn(final ListView v) {
        float destX = v.getX();
        v.setX(destX - CameraActivity.SETTING_LIST_WIDTH_1);
        ViewPropertyAnimator vp = v.animate();
        vp.translationX(destX).setDuration(ANIMATION_DURATION);
        vp.start();
    }

    public boolean isMenuBeingShown() {
        return mPopupStatus != POPUP_NONE;
    }

    public boolean isMenuBeingAnimated() {
        return mPopupStatus == POPUP_IN_ANIMATION;
    }

    public void initSwitchItem(final String prefKey, RotateImageView switcher) {
        final IconListPreference pref =
                (IconListPreference) mPreferenceGroup.findPreference(prefKey);
        if (pref == null)
            return;

        int[] iconIds = pref.getLargeIconIds();
        int resid = -1;
        int index = pref.findIndexOfValue(pref.getValue());
        if (!pref.getUseSingleIcon() && iconIds != null) {
            // Each entry has a corresponding icon.
            resid = iconIds[index];
        } else {
            // The preference only has a single icon to represent it.
            resid = pref.getSingleIcon();
        }
        switcher.setImageResource(resid);
        switcher.setVisibility(View.VISIBLE);
        mPreferences.add(pref);
        mPreferenceMap.put(pref, switcher);
        switcher.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IconListPreference pref = (IconListPreference) mPreferenceGroup
                        .findPreference(prefKey);
                if (pref == null)
                    return;
                int index = pref.findIndexOfValue(pref.getValue());
                CharSequence[] values = pref.getEntryValues();
                index = (index + 1) % values.length;
                pref.setValueIndex(index);
                ((RotateImageView) v).setImageResource(((IconListPreference) pref)
                        .getLargeIconIds()[index]);
                if (prefKey.equals(CameraSettings.KEY_CAMERA_ID))
                    mListener.onCameraPickerClicked(index);
                reloadPreference(pref);
                onSettingChanged(pref);
            }
        });
    }

    public void openFirstLevel() {
        if (mListMenu == null || mPopupStatus != POPUP_FIRST_LEVEL) {
            initializePopup();
            mPopupStatus = POPUP_FIRST_LEVEL;
        }
        mUI.showPopup(mListMenu, 1, true);
    }

    @Override
    public void overrideSettings(final String... keyvalues) {
        super.overrideSettings(keyvalues);
        if (((mListMenu == null)) || mPopupStatus != POPUP_FIRST_LEVEL) {
            mPopupStatus = POPUP_FIRST_LEVEL;
            initializePopup();
        }
        mListMenu.overrideSettings(keyvalues);

    }

    @Override
    // Hit when an item in the second-level popup gets selected
    public void onListPrefChanged(ListPreference pref) {
        if (mPopupStatus == POPUP_SECOND_LEVEL) {
            mListMenu.reloadPreference();
            animateFadeOut(mListSubMenu, 2);
        }
        super.onSettingChanged(pref);
        ((ListMenu) mListMenu).resetHighlight();
    }

    protected void initializePopup() {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        ListMenu popup1 = (ListMenu) inflater.inflate(
                R.layout.list_menu, null, false);
        popup1.setSettingChangedListener(this);
        String[] keys = mOtherKeys1;
        if (CameraActivity.isDeveloperMenuEnabled())
            keys = mOtherKeys2;
        popup1.initialize(mPreferenceGroup, keys);
        if (mActivity.isSecureCamera()) {
            // Prevent location preference from getting changed in secure camera
            // mode
            popup1.setPreferenceEnabled(CameraSettings.KEY_RECORD_LOCATION, false);
        }
        mListMenu = popup1;

    }

    public void popupDismissed(boolean topPopupOnly) {
        // if the 2nd level popup gets dismissed
        if (mPopupStatus == POPUP_SECOND_LEVEL) {
            initializePopup();
            mPopupStatus = POPUP_FIRST_LEVEL;
            if (topPopupOnly) {
                mUI.showPopup(mListMenu, 1, false);
            }
        } else {
            initializePopup();
        }
    }

    public void hideUI() {
        mFrontBackSwitcher.setVisibility(View.INVISIBLE);
    }

    public void showUI() {
        mFrontBackSwitcher.setVisibility(View.VISIBLE);
    }

    @Override
    // Hit when an item in the first-level popup gets selected, then bring up
    // the second-level popup
    public void onPreferenceClicked(ListPreference pref) {
        onPreferenceClicked(pref, 0);
    }

    @Override
    // Hit when an item in the first-level popup gets selected, then bring up
    // the second-level popup
    public void onPreferenceClicked(ListPreference pref, int y) {
        LayoutInflater inflater = (LayoutInflater) mActivity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        ListSubMenu basic = (ListSubMenu) inflater.inflate(
                R.layout.list_sub_menu, null, false);
        basic.initialize(pref, y);
        basic.setSettingChangedListener(this);
        mUI.removeLevel2();
        mListSubMenu = basic;
        if (mPopupStatus == POPUP_SECOND_LEVEL) {
            mUI.showPopup(mListSubMenu, 2, false);
        } else {
            mUI.showPopup(mListSubMenu, 2, true);
        }
        mPopupStatus = POPUP_SECOND_LEVEL;
    }

    public void onListMenuTouched() {
        mUI.removeLevel2();
    }

    public void closeView() {
        if (mUI != null)
            mUI.removeLevel2();

        if (mListMenu != null)
            animateSlideOut(mListMenu, 1);
    }
}
