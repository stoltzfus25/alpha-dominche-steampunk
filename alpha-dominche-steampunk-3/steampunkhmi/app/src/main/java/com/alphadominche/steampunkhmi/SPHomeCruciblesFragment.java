package com.alphadominche.steampunkhmi;

import java.util.Observable;
import java.util.Observer;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.alphadominche.steampunkhmi.SPModel.IOIOConnectionObserver;

public class SPHomeCruciblesFragment extends Fragment implements Observer, IOIOConnectionObserver {

    private boolean mActive;

    private View mNoConnectivityIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.sp_home_crucibles_page, container, false);

        mNoConnectivityIndicator = view.findViewById(R.id.no_connectivity_indicator);

        return view;
    }

    @Override
    public void onResume() {
        SPModel.getInstance(getActivity()).addObserver(this);

        super.onResume();

        SPModel.getInstance(getActivity()).addConnectionObserver(this);
        mActive = true;
    }

    @Override
    public void onPause() {
        mActive = false;
        SPModel.getInstance(getActivity()).deleteObserver(this);
        SPModel.getInstance(getActivity()).removeConnectionObserver(this);
        super.onPause();
    }

    @Override
    public void update(Observable arg0, Object arg1) {
        //TODO Using debug flag to toggle this on and off
        //use this for instant debug feedback at top of crucibles screen
//		double temp = SPModel.getInstance(getActivity()).getBoilerCurrentTemp();
//		TextView t = (TextView)getActivity().findViewById(R.id.home_activity_err_msg);
//		t.setText("" + temp);
    }

    public void notifyOfConnectionStatus() {
        final Activity activity = getActivity();
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SPLog.debug("got connection notification");
                if (!mActive) return;

                SPModel model = SPModel.getInstance(activity);

                if (model.isConnectedToIOIO()) {
                    mNoConnectivityIndicator.setVisibility(View.GONE);
                } else {
                    mNoConnectivityIndicator.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
