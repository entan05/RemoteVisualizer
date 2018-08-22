package jp.team.e_works.remotevisualizerclient;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class TcpConnecter {

    private String mIpAddress;
    private int mPort;

    private Socket mSocket = null;

    private TcpReceiveListener mTcpReceiveListener = null;

    private HandlerThread mReciveHandlerThread;
    private Handler mReceiveHandler;
    private HandlerThread mSendHandlerThread;
    private Handler mSendHandler;

    public TcpConnecter(@NonNull String ipAddress, int port) {
        if (TextUtils.isEmpty(ipAddress) || port < 0) {
            throw new IllegalArgumentException("Bad Argment");
        }
        mIpAddress = ipAddress;
        mPort = port;

        mReciveHandlerThread = new HandlerThread("ReciveHandlerThread");
        mReciveHandlerThread.start();
        mReceiveHandler = new Handler(mReciveHandlerThread.getLooper());

        mSendHandlerThread = new HandlerThread("SendHandlerThread");
        mSendHandlerThread.start();
        mSendHandler = new Handler(mSendHandlerThread.getLooper());
    }

    public void connect(TcpReceiveListener listener) {
        if (listener == null) {
            return;
        }
        mTcpReceiveListener = listener;

        mReceiveHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    mSocket = new Socket(mIpAddress, mPort);

                    String message;
                    BufferedReader br = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                    while (mSocket.isConnected() && (message = br.readLine()) != null) {
                        mTcpReceiveListener.onReceive(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    mTcpReceiveListener = null;
                }
            }
        });
    }

    public void disconnect() {
        mSendHandler = null;
        mSendHandlerThread.quitSafely();
        mReceiveHandler = null;
        mReciveHandlerThread.quitSafely();
    }

    public void sendMessage(final String message) {
        mSendHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mSocket != null && mSocket.isConnected()) {
                    try {
                        OutputStream outputStream = mSocket.getOutputStream();

                        byte[] data = (message + "\n").getBytes();
                        outputStream.write(data, 0, data.length);
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public interface TcpReceiveListener {
        void onReceive(String message);
    }
}
