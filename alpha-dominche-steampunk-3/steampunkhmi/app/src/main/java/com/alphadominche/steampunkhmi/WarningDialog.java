package com.alphadominche.steampunkhmi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class WarningDialog extends DialogFragment {
    public static final String TAG = WarningDialog.class.getName();

    private String mText;

    private TextView mOk;
    private TextView mWarningText;

    // The dialog does not expose its root view so null is passed.
    // This is OK because AlertDialog will erase any layoutParams on the layout
    // and replace them with match_parent
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.warning_dialog,
                null);
        builder.setView(rootView);

        mWarningText = (TextView) rootView.findViewById(R.id.warning_text);
        mWarningText.setText(mText);

        mOk = (TextView) rootView.findViewById(R.id.ok);
        mOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }

    public void setText(String text) {
        mText = text;
    }
}
