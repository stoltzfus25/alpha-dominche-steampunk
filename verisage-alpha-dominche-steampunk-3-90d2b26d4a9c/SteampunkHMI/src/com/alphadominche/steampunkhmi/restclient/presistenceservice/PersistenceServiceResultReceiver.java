package com.alphadominche.steampunkhmi.restclient.presistenceservice;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

public class PersistenceServiceResultReceiver extends ResultReceiver {

	private Receiver mReceiver;

	public PersistenceServiceResultReceiver(Handler handler) {
		super(handler);
	}

	public interface Receiver {
		public void onReceiveResult(int resultCode, Bundle resultData);

	}

	public void setReceiver(Receiver receiver) {
		mReceiver = receiver;
	}

	@Override
	protected void onReceiveResult(int resultCode, Bundle resultData) {

		if (mReceiver != null) {
			mReceiver.onReceiveResult(resultCode, resultData);
		}
	}

}