package example.com.maps;

import android.content.Intent;
import android.content.res.Configuration;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import example.com.maps.asynktask.GooglePlacesReadTask;
import example.com.maps.dto.Place;
import example.com.maps.interfaces.AsyncSearchResponse;
import example.com.maps.interfaces.AsyncTraceResponse;

public class MapsActivity extends ActionBarActivity implements LocationListener, AsyncSearchResponse, AsyncTraceResponse {
    public final static String TAG = MapsActivity.class.getName();

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private ListView mDrawerList;
    private ListView mPlacesListView;
    private ArrayAdapter<Place> mPlacesArrayAdapter;
    private List<Place> mPlacesList = new ArrayList<Place>();
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private double latitude;
    private double longitude;
    private final int PROXIMITY_RADIUS = 50000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //show error dialog if GoolglePlayServices not available

        latitude = 0; longitude = 0;

        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        setContentView(R.layout.activity_maps);
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMap = supportMapFragment.getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                String originCoordinates = "" + latitude + "," + longitude;
                String destinyCoordinates = "" + marker.getPosition().latitude + "," + marker.getPosition().longitude;

                Uri uri = Uri.parse("http://maps.google.com/maps?saddr="+originCoordinates+"&daddr="+destinyCoordinates+"&z=" + 10);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);

                return true;
            }
        });
        //Esto es para traer la ultima posicion
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        Location location = locationManager.getLastKnownLocation(bestProvider);
        if (location != null) {
            onLocationChanged(location);
        }
        //locationManager.requestLocationUpdates(bestProvider, 20000, 0, this);

        mDrawerList = (ListView)findViewById(R.id.navList);
        mPlacesListView = (ListView)findViewById(R.id.resList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();

        setPlacesAdapter();
        addListViewPlacesClickListener();
        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public void onLocationChanged(Location location) {

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    private void addDrawerItems() {
        final String[] osArray = { "Coffee", "Banks", "Apartments", "Restaurants", "Mall" , "Gym"};
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MapsActivity.this, osArray[position], Toast.LENGTH_SHORT).show();

                String type = osArray[position];
                StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
                googlePlacesUrl.append("location=" + latitude + "," + longitude);
                googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
                googlePlacesUrl.append("&name=" + type);
                googlePlacesUrl.append("&sensor=true");
                googlePlacesUrl.append("&key=" + "AIzaSyC6TQkBTYiXZNWvYhhMJhOPFurfJ7RQhA4");
                googlePlacesUrl.append("&rankBy=distance");


                GooglePlacesReadTask googlePlacesReadTask = new GooglePlacesReadTask();
                //Para pasar la referencia de la activity para implementar un callback
                //desde el AsyncTask hacia la activity
                googlePlacesReadTask.delegate = MapsActivity.this;
                Object[] toPass = new Object[2];
                toPass[0] = mMap;
                toPass[1] = googlePlacesUrl.toString();
                googlePlacesReadTask.execute(toPass);
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                mPlacesArrayAdapter.clear();
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void searchFinish(List results) {
        mPlacesList.clear();
        for (Place place : (List<Place>) results){
            mPlacesList.add(place);
            Log.v(TAG, place.toString());
        }
        mPlacesArrayAdapter.notifyDataSetChanged();
    }

    @Override
    public void traceFinish(PolylineOptions route) {

    }

    public void setPlacesAdapter(){
        mPlacesArrayAdapter = new ArrayAdapter<Place>(getApplicationContext(), R.layout.place, mPlacesList){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //return super.getView(position, convertView, parent);
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.place, parent, false);
                }

                ImageView icon = (ImageView)convertView.findViewById(R.id.place_icon);
                TextView name = (TextView)convertView.findViewById(R.id.place_name);
                TextView vicinity = (TextView)convertView.findViewById(R.id.place_vicinity);
                Place placeResult = getItem(position);

                Picasso.with(getApplicationContext()).load(placeResult.getPlaceIcon()).into(icon);
                name.setText(placeResult.getPlaceName());
                vicinity.setText(placeResult.getVicinity());
                return  convertView;
            }
        };

        mPlacesListView.setAdapter(mPlacesArrayAdapter);
    }

    private void addListViewPlacesClickListener(){
        mPlacesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Place o = (Place) adapterView.getItemAtPosition(i);
                Toast.makeText(MapsActivity.this, o.getPlaceName(), Toast.LENGTH_SHORT).show();
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(o.getLatitude(), o.getLongitude());
                markerOptions.position(latLng);
                markerOptions.title(o.getPlaceName() + " : " + o.getVicinity());
                mMap.addMarker(markerOptions);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latLng) // Center Set
                        .zoom(15.0f)                // Zoom
                        .bearing(0)                // Orientation of the camera to east
                        .tilt(40)                   // Tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

        });
    }
}
