package com.nm.themealdb;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MealAsyncTaskLoader extends AsyncTaskLoader<ArrayList<MealItems>> {

    private ArrayList<MealItems> mData;
    private boolean mHasResult = false;

    private String mJudulMeal;

    public MealAsyncTaskLoader(final Context context, String judulMeal) {
        super(context);

        onContentChanged();
        this.mJudulMeal = judulMeal;
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged())
            forceLoad();
        else if (mHasResult)
            deliverResult(mData);
    }

    //show result data
    public void deliverResult(ArrayList<MealItems> data) {
        mData = data;
        mHasResult = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (mHasResult) {
            onReleaseResources(mData);
            mData = null;
            mHasResult = false;
        }
    }

    //Api key based on account
    private static final String API_KEY = "000000000000000000000000000000";

    // Format search kota url JAKARTA = 1642911 ,BANDUNG = 1650357, SEMARANG = 1627896
    // http://api.openweathermap.org/data/2.5/group?id=1642911,1650357,1627896&units=metric&appid=API_KEY

    private void onReleaseResources(ArrayList<MealItems> data) {
    }

    @Override
    //get data synchronously
    public ArrayList<MealItems> loadInBackground() {
        SyncHttpClient client = new SyncHttpClient();

        final ArrayList<MealItems> mealses = new ArrayList<>();

        //url format to get JSON value based on title
        String url = "https://www.themealdb.com/api/json/v1/1/search.php?s=" + mJudulMeal;

        client.get(url, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                //load in background
                setUseSynchronousMode(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject responseObject = new JSONObject(result);
                    JSONArray list = responseObject.getJSONArray("meals");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject meal = list.getJSONObject(i);
                        MealItems mealItems = new MealItems(meal);
                        mealses.add(mealItems);
                    }
                } catch (Exception e) {
                    //to avoid error parsing value
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //no method if fail
            }
        });
        return mealses;
    }
}