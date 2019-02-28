package com.digits.business.dialpad;

import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.digits.business.R;
//import com.github.ali.android.client.customview.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class DialPadAnimationFragment extends Fragment implements IOnBackPressed {

    private View mOpenPadFrameBtn, mPadFrame, mCallBtn;
    private boolean mPadOpen = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dialpad_animation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mOpenPadFrameBtn = view.findViewById(R.id.num_pad_btn);
        mOpenPadFrameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPadOpen = true;
                mOpenPadFrameBtn.animate().scaleX(0f).scaleY(0f).alpha(0f)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                mPadFrame.animate().translationY(0).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCallBtn.animate().scaleX(1f).scaleY(1f).alpha(1f);
                                    }
                                });
                            }
                        });
            }
        });

        mPadFrame = view.findViewById(R.id.number_pad);
        mPadFrame.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mPadFrame.getViewTreeObserver().removeOnPreDrawListener(this);
                mPadFrame.setTranslationY(mPadFrame.getHeight());
                return true;
            }
        });

        mCallBtn = view.findViewById(R.id.call_button);
        mCallBtn.setAlpha(0f);
        mCallBtn.setScaleX(0f);
        mCallBtn.setScaleY(0f);
    }

    @Override
    public boolean onBackPressed() {
        if (mPadOpen) {
            mPadOpen = false;
            mPadFrame.animate().translationY(mPadFrame.getHeight()).withEndAction(new Runnable() {
                @Override
                public void run() {
                    mOpenPadFrameBtn.animate().scaleX(1f).scaleY(1f).alpha(1f);
                }
            });
            mCallBtn.animate().scaleX(0f).scaleY(0f).alpha(0f);
            return true;
        }
        return false;
    }
}
