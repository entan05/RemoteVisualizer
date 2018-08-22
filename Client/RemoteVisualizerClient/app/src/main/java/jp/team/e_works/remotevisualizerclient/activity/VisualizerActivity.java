package jp.team.e_works.remotevisualizerclient.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import jp.team.e_works.remotevisualizerclient.TcpConnecter;
import jp.team.e_works.remotevisualizerclient.view.VisualizerSurfaceView;

public class VisualizerActivity extends AppCompatActivity implements TcpConnecter.TcpReceiveListener {

    private VisualizerSurfaceView mSurfaceView;

    private TcpConnecter mTcpConnecter = null;

    // TODO: 仮置き
    private String mIpAddress = "192.168.0.20";
    private int mPort = 8888;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSurfaceView = new VisualizerSurfaceView(this);
        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTcpConnecter.sendMessage("xxxxx");
            }
        });
        setContentView(mSurfaceView);

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
        Bitmap bitmap = BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);
        mSurfaceView.drawBitmap(bitmap);
    }
}
