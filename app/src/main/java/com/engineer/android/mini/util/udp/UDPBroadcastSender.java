package com.engineer.android.mini.util.udp;


import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class UDPBroadcastSender {
    private static final String TAG = "UDPBroadcastSender";
    /**
     * 目标端口号，与接收端保持一致 , 接收udp广播端口 9876，下面是发送 udp广播端口
     */
    private static final int PORT = 12306; //

    /**
     * @param cuei cuei
     *             <p>
     *             cuei 和本机 cuei 相同时才返回，返回内容
     *             <p>
     *             “tt_response:$cuei:$wlanip”
     */
    public void sendBroadcast(String msg, Context context) {

        String wlanIp = getWifiIP(context);
        String message = String.format("%s from :%s ", msg, wlanIp);
        new Thread() {
            @Override
            public void run() {
                DatagramSocket socket = null;
                try {
                    socket = new DatagramSocket();
                    socket.setBroadcast(true);
                    byte[] sendData = message.getBytes();
//                    String broadcastIP2 = getBroadcastIP2();
                    String broadcastIP2 = "255.255.255.255";
                    Log.i(TAG, "broadcastIP=" + broadcastIP2);
                    InetAddress broadcastAddr = InetAddress.getByName(broadcastIP2);
                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcastAddr, PORT);
                    socket.send(sendPacket);
                    Log.i(TAG, "send message = " + message);

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                }
            }
        }.start();
    }

    public static String getWifiIP(Context context) {
        WifiManager wifimanage = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifimanage != null) {
            WifiInfo wifiinfo = wifimanage.getConnectionInfo();
            if (wifiinfo != null) {
                final int ipAddress = wifiinfo.getIpAddress();
                String ip = formatIp(ipAddress);
                Log.i(TAG, "getWifiIP IpAddress = + " + ipAddress + " ip:" + ip);
                return ip;
            }
        }
        return "";
    }

    private static String formatIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24) & 0xFF);
    }

    public static String getBroadcastIP2() {

        try {
            for (Enumeration<NetworkInterface> enumInter = NetworkInterface.getNetworkInterfaces(); enumInter.hasMoreElements(); ) {
                NetworkInterface networkInterface = enumInter.nextElement();
                if (!networkInterface.isLoopback()) {
                    for (InterfaceAddress ia : networkInterface.getInterfaceAddresses()) {
                        if (ia.getBroadcast() != null) {
                            String hostAddress = ia.getBroadcast().getHostAddress();
                            return hostAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "";
    }
}