package jp.team.e_works.remotevisualizerclient.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public class SettingManager {
    private static final String PREFERENCES_KEY = "RemoteVisualizerClient";

    private static final String KEY_IPADDRESS = "ipAddress";
    private static final String KEY_PORT = "port";

    private static SettingManager sSettingManager = new SettingManager();

    private static String sIpAddress = null;
    private static int sPort = -1;

    private SettingManager() {
    }

    public static SettingManager getInstance() {
        return sSettingManager;
    }

    public String getIpAddress(@NonNull Context context) {
        if (sIpAddress == null) {
            sIpAddress = getString(context, KEY_IPADDRESS, null);
        }
        return sIpAddress;
    }

    public void setIpAddress(@NonNull Context context, @NonNull String ipAddress) {
        if (!ipAddress.equals(sIpAddress)) {
            sIpAddress = ipAddress;
            putString(context, KEY_IPADDRESS, sIpAddress);
        }
    }

    public int getPort(@NonNull Context context) {
        if (sPort < 0) {
            sPort = getInt(context, KEY_PORT, -1);
        }
        return sPort;
    }

    public void setPort(@NonNull Context context, int port) {
        if (sPort != port) {
            sPort = port;
            putInt(context, KEY_PORT, sPort);
        }
    }

    private void putString(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private void putInt(Context context, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    private String getString(Context context, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    private int getInt(Context context, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }
}
