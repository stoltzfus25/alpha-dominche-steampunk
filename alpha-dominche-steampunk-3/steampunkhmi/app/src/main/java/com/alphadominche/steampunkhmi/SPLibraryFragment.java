/**
 *
 */
package com.alphadominche.steampunkhmi;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * @author guy
 */
public class SPLibraryFragment extends SPFragment {

    public void loadSettings(View v) {
        Intent intent = new Intent(getActivity(), SPMachineSettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.sp_library, container, false);
        return rootView;
    }
}
