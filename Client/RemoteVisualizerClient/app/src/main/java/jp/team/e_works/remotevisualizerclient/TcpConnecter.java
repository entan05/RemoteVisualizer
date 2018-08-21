package jp.team.e_works.remotevisualizerclient;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InputStream;
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

                    InputStream inputStream = mSocket.getInputStream();

                    String message;
                    do {
                        byte[] buffer = new byte[10240];
                        int n = 0;
                        int c;
                        while (n < buffer.length && mSocket.isConnected() && (c = inputStream.read()) != -1) {
                            if (c == 0x0A) {
                                break;
                            }
                            buffer[n++] = (byte) c;
                        }
                        message = new String(buffer, 0, n, "Shift_JIS");
                        mTcpReceiveListener.receive(message);
                    } while (!mSocket.isConnected());

                    inputStream.close();
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

    public interface TcpReceiveListener {
        void receive(String message);
    }
}
