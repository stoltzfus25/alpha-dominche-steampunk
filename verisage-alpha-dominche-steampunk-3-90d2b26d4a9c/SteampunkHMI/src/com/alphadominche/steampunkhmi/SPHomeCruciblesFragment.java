package com.alphadominche.steampunkhmi;

import java.util.Observable;
import java.util.Observer;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class SPHomeCruciblesFragment extends Fragment implements Observer {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		return inflater.inflate(R.layout.sp_home_crucibles_page, container, false);
	}
	
	@Override
	public void onResume() {
		SPModel.getInstance(getActivity()).addObserver(this);
		super.onResume();
	}
	
	@Override
	public void onPause() {
		SPModel.getInstance(getActivity()).deleteObserver(this);
		super.onPause();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		//use this for instant debug feedback at top of crucibles screen
//		double temp = SPModel.getInstance(getActivity()).getBoilerCurrentTemp();
//		TextView t = (TextView)getActivity().findViewById(R.id.home_activity_err_msg);
//		t.setText("" + temp);
	}
}
