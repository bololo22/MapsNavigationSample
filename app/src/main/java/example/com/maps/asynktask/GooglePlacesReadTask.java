package example.com.maps.asynktask;

/**
 * Created by Android1 on 7/21/2015.
 */
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import example.com.maps.Http;
import example.com.maps.interfaces.AsyncSearchResponse;

public class GooglePlacesReadTask extends AsyncTask<Object, Integer, String> {

    public final static String TAG = GooglePlacesReadTask.class.getName();
    public AsyncSearchResponse delegate = null;

    String googlePlacesData = null;
    GoogleMap googleMap;
    //List<String> mResults;

    @Override
    protected String doInBackground(Object... inputObj) {
        Log.i(TAG, "Doing in background");
        try {
            googleMap = (GoogleMap) inputObj[0];
            String googlePlacesUrl = (String) inputObj[1];
            //mResults = (List<String>) inputObj[2];
            Http http = new Http();
            googlePlacesData = http.read(googlePlacesUrl);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.i(TAG, "On Post Executed");
        Log.i(TAG, "RESULT: " + result);
        PlacesDisplayTask placesDisplayTask = new PlacesDisplayTask();
        placesDisplayTask.delegate = delegate;
        Object[] toPass = new Object[2];
        toPass[0] = googleMap;
        toPass[1] = result;
        //toPass[2] = mResults;
        placesDisplayTask.execute(toPass);
    }
}