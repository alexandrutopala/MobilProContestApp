package ro.infotop.journeytoself.utils;

import android.content.Context;

public final class MetricsUtil {

    private MetricsUtil() {
    }

    public static int dpToPixels(Context context, int dpValue) {
        float d = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * d); // margin in pixels
    }
}
