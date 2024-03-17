package com.alphadominche.steampunkhmi;

import java.util.TimerTask;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * @author zackious
 */
public class SPBlankingFragment extends SPFragment {
    private static final int Y_DELTA = 1;

    private static final int MIN = -1940;
    private static final int DELAY = 500;

    private boolean increase = true;
    private int hold = 0;

    private FrameLayout mWrapper;
    private FrameLayout mRevealLine;
    private FrameLayout mLogo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_blanking_screen, container, false);

        mWrapper = (FrameLayout) rootView.findViewById(R.id.blanking_screen);
        mRevealLine = (FrameLayout) rootView.findViewById(R.id.blanking_screen_revelator);
        mLogo = (FrameLayout) rootView.findViewById(R.id.blanking_screen_logo);

        mWrapper.setOnClickListener(this);
        mRevealLine.setOnClickListener(this);
        mLogo.setOnClickListener(this);

        return rootView;
    }

    private void updateView() {
        float currentY = mRevealLine.getY();
        float newY = increase ? currentY + Y_DELTA : currentY - Y_DELTA;
        mRevealLine.setY(newY);

        if (newY == 0) {
            increase = false;
            hold = DELAY;
            int visibility = mLogo.getVisibility();
            if (visibility == View.VISIBLE) {
                mLogo.setVisibility(View.INVISIBLE);
            } else {
                mLogo.setVisibility(View.VISIBLE);
            }
        } else if (newY == MIN) {
            increase = true;
            hold = DELAY;
        }
    }

    class sTask extends TimerTask {
        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (hold != 0) {
                        hold--;
                    } else {
                        updateView();
                    }
                }
            });
        }
    }

    // OnClickListener
    @Override
    public void onClick(View view) {
        getActivity().getFragmentManager().beginTransaction().remove(this).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SPActivity.notifyOfEvent();
        SPActivity.restartTimer();
    }
}
