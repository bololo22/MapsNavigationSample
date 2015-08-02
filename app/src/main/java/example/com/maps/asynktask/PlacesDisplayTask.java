package example.com.maps.asynktask;

/**
 * Created by Android1 on 7/21/2015.
 */
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import example.com.maps.Places;
import example.com.maps.dto.Place;
import example.com.maps.interfaces.AsyncSearchResponse;

public class PlacesDisplayTask extends AsyncTask<Object, Integer, List<HashMap<String, String>>> {

    public final static String TAG = PlacesDisplayTask.class.getName();

    public AsyncSearchResponse delegate = null;

    JSONObject googlePlacesJson;
    GoogleMap googleMap;
    List<Place> resultPlaces;

    @Override
    protected List<HashMap<String, String>> doInBackground(Object... inputObj) {

        Log.i(TAG, "Do In Background");

        List<HashMap<String, String>> googlePlacesList = null;
        Places placeJsonParser = new Places();

        try {
            googleMap = (GoogleMap) inputObj[0];
            googlePlacesJson = new JSONObject((String) inputObj[1]);
            //mResults = (List<String>) inputObj[2];
            googlePlacesList = placeJsonParser.parse(googlePlacesJson);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return googlePlacesList;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        Log.i(TAG, "On Post Executed");
        resultPlaces = new ArrayList<>();
        googleMap.clear();
        if (list != null && list.size() > 0) {
            Log.i(TAG, "Lista de " + list.size() + " elementos");
            resultPlaces.clear();
            for (int i = 0; i < list.size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = list.get(i);
                //Create the DTO for place
                Place place = new Place();
                place.setPlaceIcon(googlePlace.get("icon"));
                place.setPlaceName(googlePlace.get("name"));
                place.setVicinity(googlePlace.get("vicinity"));
                place.setLatitude(Double.parseDouble(googlePlace.get("lat")));
                place.setLongitude(Double.parseDouble(googlePlace.get("lng")));
                //Add Marker options to be put in the map
                //LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                //markerOptions.position(latLng);
                //markerOptions.title(place.getPlaceName() + " : " + place.getVicinity());
                //googleMap.addMarker(markerOptions);
                //Add the place to the result list
                resultPlaces.add(place);
            }
        }
        delegate.searchFinish(resultPlaces);
    }
}