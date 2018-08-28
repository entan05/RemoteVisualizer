package jp.team.e_works.remotevisualizerclient.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import jp.team.e_works.remotevisualizerclient.R;

public class ConnectServerSetupDialogFragment extends DialogFragment {

    private static final String KEY_IP = "key_ipAddress";
    private static final String KEY_PORT = "key_port";

    private ConnectServerSetupDialogListener mListener;

    @CheckResult
    public static ConnectServerSetupDialogFragment createInstance(String defIp, int defPort) {
        ConnectServerSetupDialogFragment fragment = new ConnectServerSetupDialogFragment();

        Bundle args = new Bundle();
        args.putString(KEY_IP, defIp);
        args.putInt(KEY_PORT, defPort);
        fragment.setArguments(args);

        return fragment;
    }

    public void setSetupDialogListener(ConnectServerSetupDialogListener listener) {
        mListener = listener;
    }

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String ip = null;
        int port = -1;
        if (getArguments() != null) {
            ip = getArguments().getString(KEY_IP, "");
            port = getArguments().getInt(KEY_PORT, -1);
        }

        if (getActivity() == null) {
            throw new IllegalStateException("getActivity() is null");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (inflater == null) {
            throw new IllegalStateException("inflater is null");
        }
        final View view = inflater.inflate(R.layout.layout_connect_server_setup, null);
        if (!TextUtils.isEmpty(ip)) {
            ((EditText) view.findViewById(R.id.cssd_ip_input)).setText(ip);
        }
        if (port >= 0) {
            String portStr = Integer.toString(port);
            ((EditText) view.findViewById(R.id.cssd_port_input)).setText(portStr);
        }

        builder.setView(view);
        builder.setTitle(R.string.connectServerDialog_title);
        builder.setPositiveButton(R.string.connectServerDialog_positiveBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    String newIp = ((EditText) view.findViewById(R.id.cssd_ip_input)).getText().toString();
                    int newPort = Integer.parseInt(((EditText) view.findViewById(R.id.cssd_port_input)).getText().toString());

                    if (!TextUtils.isEmpty(newIp) && newPort >= 0) {
                        mListener.onSetting(newIp, newPort);
                    }
                }
            }
        });
        builder.setNegativeButton(R.string.connectServerDialog_negativeBtn, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mListener != null) {
                    mListener.onCancel();
                }
            }
        });

        return builder.create();
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if (mListener != null) {
            mListener.onCancel();
        }
    }

    public interface ConnectServerSetupDialogListener {
        void onSetting(String ip, int port);

        void onCancel();
    }
}
