package com.alphadominche.steampunkhmi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class EditRecipeWarningDialog extends DialogFragment {
    public static final String TAG = EditRecipeWarningDialog.class.getName();

    private TextView mOk;

    // The dialog does not expose its root view so null is passed.
    // This is OK because AlertDialog will erase any layoutParams on the layout
    // and replace them with match_parent
    @SuppressLint("InflateParams")
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.edit_recipe_warning_dialog,
                null);
        builder.setView(rootView);

        mOk = (TextView) rootView.findViewById(R.id.ok);
        mOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return builder.create();
    }
}
