package com.alphadominche.steampunkhmi.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class VUID {
    private static long sLast = 0;

    public static synchronized byte[] getNext() {
        byte[] id = new byte[16];
        long current = (new Date()).getTime();
        if (current <= sLast) {
            current = sLast + 1;
        }

        sLast = current;
        byte[] mac = ByteBuffer.allocate(8).putLong(getMAC()).array();
        byte[] timeStamp = ByteBuffer.allocate(8).putLong(current).array();
        for (int i = 0; i < id.length / 2; i++) {
            id[i] = mac[i];
            id[i + id.length / 2] = timeStamp[i];
        }
        return id;
    }

    private static long getMAC() {
        long addr = 0;
        String interfaceName = "wlan0";

        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;

                byte[] mac = intf.getHardwareAddress();
                if (mac == null) {
                    addr = (new Random()).nextLong();
                } else {
                    for (int i = 0; i < mac.length; i++) {
                        addr += mac[i] * Math.pow(256, 8 - i - 1);
                    }
                }
            }
        } catch (SocketException e) {
        }

        if (addr == 0) addr = (new Random()).nextLong();

        return addr;
    }
}
