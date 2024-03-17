package com.alphadominche.steampunkhmi;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.alphadominche.steampunkhmi.restclient.persistenceservicehelper.DefaultPersistenceServiceHelper;

public class SPPasswordResetModalFragment extends DialogFragment {
    private EditText mUserNameField;
    private EditText mEmailField;
    private View mResetButton;
    private View mView;

    private SPModalDismissListener mDismissListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.sp_pw_reset_modal, null);
        builder.setView(mView);

        mUserNameField = (EditText) mView.findViewById(R.id.pw_reset_username);
        mEmailField = (EditText) mView.findViewById(R.id.pw_reset_email);
        mResetButton = mView.findViewById(R.id.pw_reset_button);

        mDismissListener = new SPModalDismissListener() {

            @Override
            public void onCancel() {
            }

            @Override
            public void onConfirm() {
                String username = mUserNameField.getText().toString();
                String email = mEmailField.getText().toString();
                String identifier = "";
                if (username != null && username.length() > 0) {
                    identifier = username;
                } else if (email != null && email.length() > 0) {
                    identifier = email;
                }
                DefaultPersistenceServiceHelper.getInstance(getActivity()).requestPasswordReset(identifier);
                dismiss();
            }
        };

        mResetButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == mResetButton) {
                    mDismissListener.onConfirm();
                }
            }
        });

        return builder.create();
    }

    public void setSPModalDismissListener(SPModalDismissListener listener) {
        mDismissListener = listener;
    }
}
