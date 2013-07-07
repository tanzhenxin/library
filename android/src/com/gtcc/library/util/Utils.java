package com.gtcc.library.util;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.app.FragmentActivity;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    private Utils() {};

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }
    
    public static ImageFetcher getImageFetcher(final FragmentActivity activity) {
        // The ImageFetcher takes care of loading remote images into our ImageView
        ImageFetcher fetcher = new ImageFetcher(activity);
        fetcher.addImageCache(activity);
        return fetcher;
    }
}