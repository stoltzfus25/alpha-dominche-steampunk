package com.alphadominche.steampunkhmi;

import java.net.SocketException;

import com.alphadominche.steampunkhmi.utils.TabletNetworkingUtil;

import android.test.AndroidTestCase;

public class TabletNewtorkUtilTest extends AndroidTestCase {
    public void testGetMacAddress(){
	String macAddress="";
	try {
		macAddress = TabletNetworkingUtil.getMACAddress();
	} catch (SocketException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	assert(!macAddress.equals(""));
    }
}
