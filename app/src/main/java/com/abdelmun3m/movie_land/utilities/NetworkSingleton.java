package com.abdelmun3m.movie_land.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by abdelmun3m on 22/01/18.
 *
 * code provided from
 * https://developer.android.com/training/volley/requestqueue.html
 * https://www.androidhive.info/2014/05/android-working-with-volley-library-1/s
 */

public class NetworkSingleton {

    private static NetworkSingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;


    private NetworkSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);
                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized NetworkSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new NetworkSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(tag);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(String tag) {
        if (getRequestQueue() != null) {
            getRequestQueue().cancelAll(tag);
        }
    }
    public ImageLoader getImageLoader() {
        return mImageLoader;
    }
}
