package jp.team.e_works.remotevisualizerclient.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import jp.team.e_works.remotevisualizerclient.R;
import jp.team.e_works.remotevisualizerclient.TcpConnecter;
import jp.team.e_works.remotevisualizerclient.view.VisualizerView;

public class VisualizerActivity extends AppCompatActivity implements TcpConnecter.TcpReceiveListener {

    private VisualizerView mVisualizer;

    private TcpConnecter mTcpConnecter = null;

    private Handler mUIHandler;

    // TODO: 仮置き
    private String mIpAddress = "192.168.0.20";
    private int mPort = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUIHandler = new Handler(getMainLooper());

        setContentView(R.layout.activity_visualizer);

        mVisualizer = findViewById(R.id.visualizer);

        mTcpConnecter = new TcpConnecter(mIpAddress, mPort);
        mTcpConnecter.connect(this);
    }

    @Override
    protected void onDestroy() {
        mTcpConnecter.disconnect();

        super.onDestroy();
    }

    @Override
    public void onReceive(String message) {
        Log.d("VisualizerActivity", "received");
        byte[] dataBytes = Base64.decode(message, 0);
        final Bitmap bitmap = BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);

        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                mVisualizer.updateDrawBitmap(bitmap);
            }
        });
    }
}
