package jp.team.e_works.remotevisualizerclient.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.WindowManager;

import jp.team.e_works.remotevisualizerclient.R;
import jp.team.e_works.remotevisualizerclient.TcpConnecter;
import jp.team.e_works.remotevisualizerclient.fragment.ConnectServerSetupDialogFragment;
import jp.team.e_works.remotevisualizerclient.util.SettingManager;
import jp.team.e_works.remotevisualizerclient.view.VisualizerView;

public class VisualizerActivity extends AppCompatActivity implements TcpConnecter.TcpReceiveListener {

    private VisualizerView mVisualizer;

    private TcpConnecter mTcpConnecter = null;

    private Handler mUIHandler;

    private SettingManager mSettingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUIHandler = new Handler(getMainLooper());

        setContentView(R.layout.activity_visualizer);

        mVisualizer = findViewById(R.id.visualizer);

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
    public void onReceive(final String message) {
        mUIHandler.post(new Runnable() {
            @Override
            public void run() {
                byte[] dataBytes = Base64.decode(message, 0);
                final Bitmap bitmap = BitmapFactory.decodeByteArray(dataBytes, 0, dataBytes.length);
                mVisualizer.updateDrawBitmap(bitmap);
            }
        });
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
