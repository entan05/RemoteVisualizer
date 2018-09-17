package jp.team.e_works.remotevisualizerclient.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import jp.team.e_works.remotevisualizerclient.Const;
import jp.team.e_works.remotevisualizerclient.R;
import jp.team.e_works.remotevisualizerclient.TcpConnecter;
import jp.team.e_works.remotevisualizerclient.fragment.ConnectServerSetupDialogFragment;
import jp.team.e_works.remotevisualizerclient.util.KeyCode;
import jp.team.e_works.remotevisualizerclient.util.SettingManager;
import jp.team.e_works.remotevisualizerclient.util.Util;
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

        if (Util.isTablet(this)) {
            setContentView(R.layout.activity_visualizer_tablet);
        } else {
            setContentView(R.layout.activity_visualizer_phone);
        }

        Button enterBtn = findViewById(R.id.enter_btn);
        enterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTcpConnecter != null) {
                    mTcpConnecter.sendMessage(Const.KEY_EVENT_PREFIX + KeyCode.ENTER.getWindowsKeyCode());
                }
            }
        });

        Button mouseRightBtn = findViewById(R.id.right_btn);
        mouseRightBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTcpConnecter != null) {
                    mTcpConnecter.sendMessage(Const.RIGHT_CLICK_PREFIX);
                }
            }
        });

        mVisualizer = findViewById(R.id.visualizer);
        mVisualizer.setOnTouchEventListener(mTouchEventListener);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        View decor = getWindow().getDecorView();
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

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

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        KeyCode keyCode = KeyCode.getKeyCodeByAndroidKeyCode(event.getKeyCode());
        if (keyCode != KeyCode.UNKNOWN) {
            if (mTcpConnecter != null) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mTcpConnecter.sendMessage(Const.KEY_DOWN_PREFIX + keyCode.getWindowsKeyCode());
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP) {
                    mTcpConnecter.sendMessage(Const.KEY_UP_PREFIX + keyCode.getWindowsKeyCode());
                    return true;
                }
            }
        }

        return super.dispatchKeyEvent(event);
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

    private VisualizerView.OnTouchEventListener mTouchEventListener = new VisualizerView.OnTouchEventListener() {
        @Override
        public void onEvent(int touchType, int x, int y) {
            if (mTcpConnecter != null) {
                StringBuilder sb = new StringBuilder();
                switch (touchType) {
                    case Const.TOUCH_TYPE_DOWN:
                        sb.append(Const.TOUCH_DOWN_PREFIX);
                        break;

                    case Const.TOUCH_TYPE_MOVE:
                        sb.append(Const.TOUCH_MOVE_PREFIX);
                        break;

                    case Const.TOUCH_TYPE_UP:
                        sb.append(Const.TOUCH_UP_PREFIX);
                        break;

                    default:
                        return;
                }
                sb.append(x);
                sb.append(",");
                sb.append(y);

                mTcpConnecter.sendMessage(sb.toString());
            }
        }
    };
}
