package com.alphadominche.steampunkhmi.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.List;


public class TabletNetworkingUtil {
    private static String macAddress = "";

    public static String getMACAddress() throws SocketException {
        if (macAddress.equals("")) {
            macAddress = obtainMACAddress();
        }
        return macAddress;
    }

    private static String obtainMACAddress() throws SocketException {
        String interfaceName = "wlan0";

        List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
        for (NetworkInterface intf : interfaces) {
            if (interfaceName != null) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
            }
            byte[] mac = intf.getHardwareAddress();
            if (mac == null) return "";
            StringBuilder buf = new StringBuilder();
            for (int idx = 0; idx < mac.length; idx++)
                buf.append(String.format("%02X:", mac[idx]));
            if (buf.length() > 0) buf.deleteCharAt(buf.length() - 1);
            return buf.toString();
        }
        return "";

    }
}
