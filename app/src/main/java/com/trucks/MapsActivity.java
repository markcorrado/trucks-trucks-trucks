package com.trucks;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Outline;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity {

    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final float TWO_MILES = 3218;
    private static final String TAG = "Maps Activity";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Location mLastKnownLocation;
    private Marker currentLocationMarker;
    private ArrayList<FoodTruck> foodTruckArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();

        ImageButton refreshButton = (ImageButton) findViewById(R.id.refreshButton);

        // Check if we're running on Android 5.0 or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Call some material design APIs here
            ViewOutlineProvider viewOutlineProvider = new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    // Or read size directly from the view's width/height
                    outline.setOval(0, 0, view.getWidth(), view.getHeight());
                }
            };
            refreshButton.setOutlineProvider(viewOutlineProvider);
            refreshButton.setClipToOutline(true);
        } else {
            // Implement features without material design
        }
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRealFoodTrucks();
            }
        });
        loadMap();
    }

    private void loadMap(){
        if(mMap != null) {
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Toast.makeText(MapsActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
            if(foodTruckArrayList != null) {
                addFoodTrucksToMap();
            }
            else {
//                getTestFoodTrucks();
                getRealFoodTrucks();
            }
        }
        else {
            setUpMapIfNeeded();
        }
    }

    private void getTestFoodTrucks(){
        foodTruckArrayList = new ArrayList<FoodTruck>();
        foodTruckArrayList.add(new FoodTruck("Maria Bonita", 41.256698, -95.932180, "Tacos"));
        foodTruckArrayList.add(new FoodTruck("Voo Doo ", 41.284694, -96.006109, "Tacos"));
        foodTruckArrayList.add(new FoodTruck("Localmotive", 41.254618, -95.931594, "Rounders"));
        foodTruckArrayList.add(new FoodTruck("Island Seasons", 41.257807, -95.973217, "Hawaii"));
        foodTruckArrayList.add(new FoodTruck("402 BBQ", 41.249416, -96.023061, "BBQ"));
        loadMap();
    }

    private void getRealFoodTrucks(){
        TrucksRestClient restClient = TrucksRestClient.getInstance(this, getString(R.string.server));
        RequestParams params = new RequestParams();
        Toast.makeText(this, "Finding Foodtrucks...", Toast.LENGTH_SHORT).show();

        restClient.get("trucksnearme.json", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
                foodTruckArrayList = new ArrayList<FoodTruck>();
                for (int i=0; i < response.length(); i++)
                {
                    try {
                        JSONObject truckObject = response.getJSONObject(i);
                        // Pulling items from the array
                        String name = truckObject.getString("title");
                        String copy = truckObject.getString("text");
                        String latitude = truckObject.getString("latitude");
                        String longitude = truckObject.getString("longitude");
                        foodTruckArrayList.add(new FoodTruck(name, Double.parseDouble(latitude), Double.parseDouble(longitude), copy));
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception" + e);
                    }
                }
                loadMap();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Toast.makeText(getApplicationContext(), "Error posting buzz!", Toast.LENGTH_LONG).show();
                System.out.println(errorResponse);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    private void addFoodTrucksToMap(){
        for (FoodTruck truck : foodTruckArrayList) {
            mMap.addMarker(new MarkerOptions().position(new LatLng(truck.getLatitude(), truck.getLongitude()))
                    .title(truck.getName())
                    .snippet(truck.getCopy())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pickup)));
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                //makeUseOfNewLocation(location);
                if(isBetterLocation(location, mLastKnownLocation)) {
                    if(currentLocationMarker != null) {
                        currentLocationMarker.remove();
                    }
                    mLastKnownLocation = location;
                    currentLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude())).title("You are here"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), 16));
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates( Provider, min time between updates, max distance between updates, listener)
        locationManager.requestLocationUpdates(locationProvider, TWO_MINUTES, TWO_MILES, locationListener);
        loadMap();
    }


    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
