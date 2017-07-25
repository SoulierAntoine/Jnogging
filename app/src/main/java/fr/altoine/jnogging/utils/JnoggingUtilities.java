package fr.altoine.jnogging.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

/**
 * Created by soulierantoine on 02/07/2017.
 */

public class JnoggingUtilities {
    /**
     * @deprecated
     */
    public static boolean checkPermission(Context context, String requestedPermission) {
        return (ContextCompat.checkSelfPermission(context, requestedPermission) != PackageManager.PERMISSION_GRANTED);
    }

    // TODO
    public static int getDistanceFromLocation(Location startingLocation, Location endingLocation) {
        // https://maps.googleapis.com/maps/api/directions/json?origin=Toronto&destination=Montreal&mode=walking
        return 10;
    }

    public static int safeLongToInt(long l) {
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE)
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");

        return (int) l;
    }
}
