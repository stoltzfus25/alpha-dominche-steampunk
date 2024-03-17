package com.alphadominche.steampunkhmi;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class SPAgListViewManager extends Observable implements Observer {
    public final static String START_TIME_TITLE = "Start Time";
    public final static String DURATION_TITLE = "Duration";
    public final static String PULSE_WIDTH_TITLE = "Pulse Width";

    public final static float DIM = 0.5f;
    public final static float BRIGHT = 1.0f;

    private View mButtonContainer;
    private FrameLayout mGraphicContainer;
    private ArrayList<View> mGraphics;
    private View mTrashAgBtn;
    private View mLeftAgBtn;
    private View mEditAgBtn;
    private View mRightAgBtn;
    private View mAddAgBtn;
    private SPRecipe mRecipe;

    private SPActivity mActivity; //this is intended to be the activity this is embedded in
    private OnClickListener mAddAgListener;
    private OnClickListener mEditAgListener;
    private OnClickListener mDeleteAgListener;
    private OnClickListener mLeftAgListener;
    private OnClickListener mRightAgListener;

    private OnClickListener mNoResponse;

    private int mSelectedAg;

    private int mStackIndex;

    public SPAgListViewManager(View buttonContainer, FrameLayout graphicContainer, SPRecipe recipe, int stackIndex, SPActivity activity) {
        mButtonContainer = buttonContainer;
        mGraphicContainer = graphicContainer;
        mActivity = activity;

        mSelectedAg = 0;

        mTrashAgBtn = mButtonContainer.findViewById(R.id.trash_ag_button);
        mLeftAgBtn = mButtonContainer.findViewById(R.id.left_ag_button);
        mEditAgBtn = mButtonContainer.findViewById(R.id.edit_ag_button);
        mRightAgBtn = mButtonContainer.findViewById(R.id.right_ag_button);
        mAddAgBtn = mButtonContainer.findViewById(R.id.add_ag_button);

        mGraphics = new ArrayList<View>();

        mRecipe = recipe;
        mStackIndex = stackIndex;

        mAddAgBtn.setOnClickListener(mAddAgListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRecipe.getAgitationCount(mStackIndex) >= 10) {
                    return; //only 10 agitations allowed
                }
                int startTime = nextStartTime();
                if (!makeRoomForNewAg()) {
                    return; //no room to make an ag here!
                }
                mRecipe.setAgitation(mStackIndex, ++mSelectedAg, SPRecipeDefaults.EXTRA_AGITATION, startTime, SPRecipeDefaults.PULSE_WIDTH);
                SPAgitationEditorModalFragment modal = new SPAgitationEditorModalFragment();
                String units = mActivity.getString(R.string.seconds_abbreviation);
                modal.show(mActivity.getFragmentManager(), "c");
                SPRecipeAgSliderInfo info = new SPRecipeAgSliderInfo(mRecipe, mStackIndex, mSelectedAg);
                modal.setInfo(info);
                modal.setStartLimitsUnitsAndTitle(info.getStartTimeMin(), info.getStartTimeMax(), units, START_TIME_TITLE);
                modal.setDurationLimitsUnitsAndTitle(info.getDurationMin(), info.getDurationMax(), units, DURATION_TITLE);
                modal.setPulseLimitsUnitsAndTitle(info.getPulseWidthMin(), info.getPulseWidthMax(), units, PULSE_WIDTH_TITLE);

                modal.setStartTimeResponder(getStartTimeResponder(mSelectedAg), info.getStartTimeProgressMax(), info.getStartTimeProgress());

                modal.setDurationResponder(getDurationResponder(mSelectedAg), info.getDurationProgressMax(), info.getDurationProgress());

                modal.setPulseWidthResponder(getPulseWidthResponder(mSelectedAg), info.getPulseWidthProgressMax(), info.getPulseWidthProgress());
            }
        });

        mEditAgBtn.setOnClickListener(mEditAgListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                SPRecipeAgSliderInfo info = new SPRecipeAgSliderInfo(mRecipe, mStackIndex, mSelectedAg);
                SPAgitationEditorModalFragment modal = new SPAgitationEditorModalFragment();
                String units = mActivity.getString(R.string.seconds_abbreviation);
                modal.setInfo(info);
                modal.setStartLimitsUnitsAndTitle(info.getStartTimeMin(), info.getStartTimeMax(), units, START_TIME_TITLE);
                modal.setDurationLimitsUnitsAndTitle(info.getDurationMin(), info.getDurationMax(), units, DURATION_TITLE);
                modal.setPulseLimitsUnitsAndTitle(info.getPulseWidthMin(), info.getPulseWidthMax(), units, PULSE_WIDTH_TITLE);
                modal.show(mActivity.getFragmentManager(), "agModal");

                modal.setStartTimeResponder(getStartTimeResponder(mSelectedAg), info.getStartTimeProgressMax(), info.getStartTimeProgress());

                modal.setDurationResponder(getDurationResponder(mSelectedAg), info.getDurationProgressMax(), info.getDurationProgress());

                modal.setPulseWidthResponder(getPulseWidthResponder(mSelectedAg), info.getPulseWidthProgressMax(), info.getPulseWidthProgress());
            }
        });

        mTrashAgBtn.setOnClickListener(mDeleteAgListener = new OnClickListener() {
            @Override
            public void onClick(View v) { //SPLog.debug("got delete click!");
                if (mGraphics.size() <= 1) return; //do not let the user delete the last agitation
                //confirm delete dialog
                AlertDialog.Builder builder = new android.app.AlertDialog.Builder(mActivity, AlertDialog.THEME_HOLO_DARK);
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setTitle(mActivity.getResources().getString(R.string.delete_capitalized));
                builder.setMessage(mActivity.getResources().getString(R.string.delete_agitation_confirm_question));
                builder.setPositiveButton(mActivity.getResources().getString(R.string.yes_capitalized), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        delete();
                    }

                    private void delete() {
                        int agToDelete = mSelectedAg;
                        if (--mSelectedAg < 0) {
                            mSelectedAg = 0;
                        }
                        mRecipe.removeAgitation(mStackIndex, agToDelete);
                        if (mSelectedAg == 0) { //this stuff needs to go into the model
                            double length = mRecipe.getAgitationLength(mStackIndex, mSelectedAg);
                            int startTime = 0;
                            double pulseWidth = mRecipe.getAgitationPulseWidth(mStackIndex, mSelectedAg);
                            mRecipe.setAgitation(mStackIndex, mSelectedAg, length, startTime, pulseWidth);
                        }
                    }
                });
                builder.setNegativeButton(mActivity.getResources().getString(R.string.no_capitalized), null);
                builder.show();
            }
        });

        mLeftAgBtn.setOnClickListener(mLeftAgListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (--mSelectedAg <= 0) mSelectedAg = 0;
                update(null, null);
            }
        });

        mRightAgBtn.setOnClickListener(mRightAgListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                int lastIndex = mRecipe.getAgitationCount(mStackIndex) - 1;
                if (++mSelectedAg >= lastIndex) mSelectedAg = lastIndex;
                update(null, null);
            }
        });

        mNoResponse = new OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mGraphics.size(); i++) {
                    View target = mGraphics.get(i).findViewById(R.id.link);
                    if (target == v) {
                        mSelectedAg = i;
                        update(null, null);
                        break;
                    }
                }
            }
        };

        update(null, null);
    }

    private boolean makeRoomForNewAg() {
        if (!hasEnoughRoomToAddAg()) {
            return false;
        }
        int topAg = mRecipe.getAgitationCount(mStackIndex);
        for (int i = topAg; i > mSelectedAg; i--) {
            double seconds = mRecipe.getAgitationLength(mStackIndex, i - 1);
            int startTime = mRecipe.getAgitationStartTime(mStackIndex, i - 1);
            double pulseWidth = mRecipe.getAgitationPulseWidth(mStackIndex, i - 1);
            mRecipe.setAgitation(mStackIndex, i, seconds, startTime, pulseWidth);
        }
        return true;
    }

    private SPSliderModalResponder getStartTimeResponder(final int index) {
        SPSliderModalResponder startTimeResponder = new SPSliderModalResponder() {
            @Override
            public void setValue(int value) {
                double length = mRecipe.getAgitationLength(mStackIndex, index);
                double pulseWidth = mRecipe.getAgitationPulseWidth(mStackIndex, index);
                int startTime = value + (new SPRecipeAgSliderInfo(mRecipe, mStackIndex, index)).getStartTimeMin();
                mRecipe.setAgitation(mStackIndex, index, length, startTime, pulseWidth);
            }
        };
        return startTimeResponder;
    }

    private SPSliderModalResponder getDurationResponder(final int index) {
        SPSliderModalResponder durationResponder = new SPSliderModalResponder() {
            @Override
            public void setValue(int value) {
                int startTime = mRecipe.getAgitationStartTime(mStackIndex, index);
                double pulseWidth = mRecipe.getAgitationPulseWidth(mStackIndex, index);
                double length = value / 10.0;
                mRecipe.setAgitation(mStackIndex, index, length, startTime, pulseWidth);
            }
        };
        return durationResponder;
    }

    private SPSliderModalResponder getPulseWidthResponder(final int index) {
        SPSliderModalResponder pulseWidthResponder = new SPSliderModalResponder() {
            @Override
            public void setValue(int value) {
                double length = mRecipe.getAgitationLength(mStackIndex, index);
                int startTime = mRecipe.getAgitationStartTime(mStackIndex, index);
                double pulseWidth = value / 10.0;
                mRecipe.setAgitation(mStackIndex, index, length, startTime, pulseWidth);
            }
        };
        return pulseWidthResponder;
    }

    public int nextStartTime() {
        SPRecipeAgSliderInfo info = new SPRecipeAgSliderInfo(mRecipe, mStackIndex, mSelectedAg);
        return info.getNextStartTime();
    }

    public boolean roomLeftInStack() {
        return nextStartTime() < mRecipe.getTotalTime(mStackIndex);
    }

    public void setRecipe(SPRecipe recipe) {
        if (mRecipe != recipe) {
            mStackIndex = -1;
        }
        mRecipe = recipe;
        mRecipe.addObserver(this);
    }

    public void setStackIndex(int stackIndex) {
        if (mStackIndex != stackIndex) {
            mSelectedAg = 0; //need to reset the selection when changing stacks
        }
        mStackIndex = stackIndex;
        if (mSelectedAg >= mRecipe.getAgitationCount(mStackIndex)) { //last ag may have been removed!
            mSelectedAg = mRecipe.getAgitationCount(mStackIndex) - 1; //next to last ag
        }
        update(null, null);
    }

    @Override
    public void update(Observable observable, Object data) { //SPLog.debug("*** TIMELINE IS GETTING UPDATED ***");
        //make sure the right number of buttons and graphics exist...
        //SPLog.debug("buttons: " + mGraphics.size() + " ags: " + mRecipe.getAgitationCount(mStackIndex));
        while (mRecipe.getAgitationCount(mStackIndex) > mGraphics.size()) { //SPLog.debug("adding ag GUI");
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View newGraphic = inflater.inflate(R.layout.agitation_graphic, mGraphicContainer, false);
            mGraphicContainer.addView(newGraphic);
            mGraphics.add(newGraphic);
        }
        while (mRecipe.getAgitationCount(mStackIndex) < mGraphics.size()) { //SPLog.debug("removing ag GUI");
            mGraphicContainer.removeView(mGraphics.remove(mGraphics.size() - 1));
        }

        //make sure the buttons and graphics show the right things...
        for (int i = 0; i < mRecipe.getAgitationCount(mStackIndex); i++) {
            SPRecipeAgSliderInfo info = new SPRecipeAgSliderInfo(mRecipe, mStackIndex, i);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mGraphics.get(i).getLayoutParams();
            int parentWidth = mGraphicContainer.getMeasuredWidth();
            double pixelScale = (double) parentWidth / (double) mRecipe.getTotalTime(mStackIndex);
            params.setMargins((int) (pixelScale * mRecipe.getAgitationStartTime(mStackIndex, i)), params.topMargin, params.rightMargin, params.bottomMargin);
            params.width = (int) (SPModel.MAX_AGITATION_LENGTH * pixelScale);
            SeekBar bar = (SeekBar) mGraphics.get(i).findViewById(R.id.length_slider);
            bar.getLayoutParams().width = (int) (params.width);
            View cover = mGraphics.get(i).findViewById(R.id.link);
            cover.getLayoutParams().width = params.width;
            cover.setOnClickListener(mNoResponse);
//			SPLog.debug("pixel width of graphic: " + (int)(SPModel.MAX_AGITATION_LENGTH * pixelScale) + " maxLen: " + SPModel.MAX_AGITATION_LENGTH + " pixelScale: " + pixelScale);
            mGraphics.get(i).setLayoutParams(params);
            bar.setProgress(info.getDurationProgress()); //SPLog.debug("progress: " + info.getDurationProgress() + " duration: " + info.getDuration() + " maxProg: " + info.getDurationProgressMax());
            ((TextView) mGraphics.get(i).findViewById(R.id.time_label)).setText(formatDouble(mRecipe.getAgitationLength(mStackIndex, i), 1));
            mGraphics.get(i).setAlpha(i == mSelectedAg ? BRIGHT : DIM);
        }

        if (hasAgsToTheLeft()) {
            mLeftAgBtn.setAlpha(BRIGHT);
        } else {
            mLeftAgBtn.setAlpha(DIM);
        }

        if (hasAgsToTheRight()) {
            mRightAgBtn.setAlpha(BRIGHT);
        } else {
            mRightAgBtn.setAlpha(DIM);
        }

        if (hasMoreThanOneAg()) {
            mTrashAgBtn.setAlpha(BRIGHT);
        } else {
            mTrashAgBtn.setAlpha(DIM);
        }

        if (hasEnoughRoomToAddAg()) {
            mAddAgBtn.setAlpha(BRIGHT);
        } else {
            mAddAgBtn.setAlpha(DIM);
        }
    }

    private boolean hasAgsToTheLeft() {
        return mSelectedAg != 0;
    }

    private boolean hasAgsToTheRight() {
        return mSelectedAg < (mRecipe.getAgitationCount(mStackIndex) - 1);
    }

    private boolean hasMoreThanOneAg() {
        return mRecipe.getAgitationCount(mStackIndex) > 1;
    }

    private boolean hasEnoughRoomToAddAg() {
        return (new SPRecipeAgSliderInfo(mRecipe, mStackIndex, mSelectedAg)).hasRoomAfterToAddAg();
    }

    public static String formatDouble(double val, int decimalPlaces) {
        return String.format("%." + decimalPlaces + "f", val);
    }
}
