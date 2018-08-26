package jp.team.e_works.remotevisualizerclient.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import jp.team.e_works.remotevisualizerclient.TcpConnecter;
import jp.team.e_works.remotevisualizerclient.fragment.ConnectServerSetupDialogFragment;
import jp.team.e_works.remotevisualizerclient.util.SettingManager;
import jp.team.e_works.remotevisualizerclient.view.VisualizerSurfaceView;

public class VisualizerActivity extends AppCompatActivity implements TcpConnecter.TcpReceiveListener {

    private VisualizerSurfaceView mSurfaceView;

    private TcpConnecter mTcpConnecter = null;

    private SettingManager mSettingManager;

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

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSettingManager = SettingManager.getInstance();

        ConnectServerSetupDialogFragment dialog = ConnectServerSetupDialogFragment.createInstance(
                mSettingManager.getIpAddress(this),
                mSettingManager.getPort(this));
        dialog.setSetupDialogListener(mCSSDListener);
        dialog.show(getSupportFragmentManager(), "ConnectServerSetupDialog");
    }

    @Override
    protected void onDestroy() {
        if (mTcpConnecter != null) {
            mTcpConnecter.disconnect();
        }

        super.onDestroy();
    }

    @Override
    public void onReceive(String message) {
        Log.d("VisualizerActivity", "received");
        byte[] dataBytes = Base64.decode(message, 0);
        Bitmap bitmap = BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);
        mSurfaceView.drawBitmap(bitmap);
    }

    private ConnectServerSetupDialogFragment.ConnectServerSetupDialogListener mCSSDListener
            = new ConnectServerSetupDialogFragment.ConnectServerSetupDialogListener() {
        @Override
        public void onSetting(String ip, int port) {
            mSettingManager.setIpAddress(VisualizerActivity.this, ip);
            mSettingManager.setPort(VisualizerActivity.this, port);

            mTcpConnecter = new TcpConnecter(ip, port);
            mTcpConnecter.connect(VisualizerActivity.this);
        }

        @Override
        public void onCancel() {
            finish();
        }
    };
}
