package com.engineer.other.udp;


import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPBroadcastReceiver {

    private static final int PORT = 12306;
    private static final String HOST = "255.255.255.255";

    private static final String TAG = "UDPBroadcastReceiver";
    private DatagramSocket socket;
    private boolean isRunning = false;

    private HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
    private Handler handler;


    public void init() {
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {

                // 在这里处理消息
                if (!(msg.obj instanceof String)) {
                    return;
                }
                String cmd = (String) msg.obj;
                if (TextUtils.isEmpty(cmd)) {
                    return;
                }

                Log.i(TAG, "cmd error $cmd");
            }
        };
    }

    public UDPBroadcastReceiver() {
        // 初始化 socket
        try {
            InetAddress inetAddress = InetAddress.getByName(HOST);
            socket = new DatagramSocket(PORT, inetAddress);
            socket.setBroadcast(true);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    public void startListening() {
        if (!isRunning) {
            Log.i(TAG, "udp server start listen");
            isRunning = true;
            new ListenThread().start();
        }
    }

    public void stopListening() {
        isRunning = false;
        if (socket != null && !socket.isClosed()) {
            Log.i(TAG, "udp server stop listen");
            socket.close();
        }
        if (handlerThread != null) {
            handlerThread.quitSafely();
        }
    }

    class ListenThread extends Thread {
        @Override
        public void run() {

            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            Log.i(TAG, "start listen udp broadcast");


            while (isRunning) {
                try {
                    if (socket == null) {
                        break;
                    }
                    socket.receive(packet);
                    InetAddress address = packet.getAddress();

                    int port = packet.getPort();
                    String receivedCMD = new String(packet.getData(), 0, packet.getLength());


                    if (handler != null) {
                        Message message = handler.obtainMessage();
                        message.obj = receivedCMD;
                        handler.sendMessage(message);
                        Log.i(TAG, "handler send msg");
                    }
                    Log.i(TAG, "recv udp broadcast=" + receivedCMD + "- from ip=" + address + " port=" + port);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

}
