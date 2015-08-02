package example.com.maps.asynktask;

/**
 * Created by Android1 on 7/23/2015.
 */
import android.os.AsyncTask;
import android.util.Log;

import example.com.maps.Http;
import example.com.maps.interfaces.AsyncTraceResponse;

public class ReadPointsTask extends AsyncTask<String, Void, String> {

    public final static String TAG = ReadPointsTask.class.getName();

    public AsyncTraceResponse delegate = null;

    @Override
    protected String doInBackground(String... url) {
        String data = "";
        try {
            Http http = new Http();
            data = http.read(url[0]);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return data;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        TraceRouteTask traceRouteTask = new TraceRouteTask();
        traceRouteTask.delegate = delegate;
        traceRouteTask.execute(result);
    }
}