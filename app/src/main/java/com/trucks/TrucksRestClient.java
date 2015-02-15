package com.trucks;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by markcorrado on 2/15/15.
 */
public class TrucksRestClient extends AsyncHttpClient {

    private static  String mBaseApiUrl;
    private static TrucksRestClient mClient = null;
    private static Context mContext;

    public static TrucksRestClient getInstance(Context context, String baseApiUrl) {
        if(mClient == null) {
            mClient = new TrucksRestClient();
        }
        mBaseApiUrl = baseApiUrl;
        mContext = context;
        return mClient;
    }

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        super.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        super.post(getAbsoluteUrl(url), params, responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return mBaseApiUrl + relativeUrl;
    }
}