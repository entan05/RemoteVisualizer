package jp.team.e_works.remotevisualizerclient.util;

import android.content.Context;

public class Util {
    public static boolean isTablet(Context context) {
        return context.getResources().getConfiguration().smallestScreenWidthDp >= 600;
    }
}
