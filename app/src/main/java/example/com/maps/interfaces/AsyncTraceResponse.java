package example.com.maps.interfaces;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Android1 on 7/23/2015.
 */
public interface AsyncTraceResponse {
    void traceFinish(PolylineOptions route);
}
