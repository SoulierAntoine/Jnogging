package fr.altoine.jnogging.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by soulierantoine on 02/07/2017.
 */

public class JnoggingUtilities {
    public static boolean checkPermission(Context context, String requestedPermission) {
        return (ContextCompat.checkSelfPermission(context, requestedPermission) != PackageManager.PERMISSION_GRANTED);

    }
}
