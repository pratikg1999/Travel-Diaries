package com.example.android.traveldiaries;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    LocationManager locationManager;
    LocationListener locationListener;
    private GoogleMap mMap;
    Intent intent;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            if(intent!=null && intent.getIntExtra("placeholder",0)==0) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "You are here");
            }
        }
    }

    public void centerMapOnLocation(Location location, String title){
        if(location!=null) {
            LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(userLocation).title(title));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 6));
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("map", "onCreate: Map  opened");
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        intent = getIntent();
        Toast.makeText(this, ""+ intent.getStringExtra("place"), Toast.LENGTH_SHORT).show();
        mMap.setOnMapLongClickListener(this);
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
            if(intent.getIntExtra("placeholder",0)==0){
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                centerMapOnLocation(lastKnownLocation, "you are here");
            }
            else {
                Location placeLocation = new Location(LocationManager.GPS_PROVIDER);
                placeLocation.setLatitude(MainActivity.locations.get(intent.getIntExtra("placeholder", 0)).latitude);
                placeLocation.setLongitude(MainActivity.locations.get(intent.getIntExtra("placeholder", 0)).longitude);
                Toast.makeText(this, placeLocation.getLatitude() + "  " + placeLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                centerMapOnLocation(placeLocation, intent.getStringExtra("place"));
            }
        }

        // Add a marker in Sydney and move the camera
        /*
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        */
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        String address = "";
        try {
            List<Address> listAddress = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if(listAddress!=null && listAddress.size()>0){
                if(listAddress.get(0).getThoroughfare()!=null){
                    if(listAddress.get(0).getSubThoroughfare()!=null){
                        address+=listAddress.get(0).getSubThoroughfare() + " ";
                    }
                    address+=listAddress.get(0).getThoroughfare();
                }
            }
            if(address.equals("")){
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy");
                address = sdf.format(new Date());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        mMap.addMarker(new MarkerOptions().position(latLng).title(address));
        MainActivity.myDataset.add(address);
        MainActivity.locations.add(latLng);
        MainActivity.mAdapter.notifyDataSetChanged();

        SharedPreferences sharedPreferences = this.getSharedPreferences("com.example.android.traveldiaries", Context.MODE_PRIVATE);
        ArrayList<String> latitudes = new ArrayList<>();
        ArrayList<String> longitudes = new ArrayList<>();
        for (LatLng loc: MainActivity.locations){
            latitudes.add(""+loc.latitude);
            longitudes.add(""+loc.longitude);

        }
        printArray(latitudes);
        printArray(longitudes);
        try {
            sharedPreferences.edit().putString("places", ObjectSerializer.serialize(MainActivity.myDataset)).apply();
            sharedPreferences.edit().putString("latitudes", ObjectSerializer.serialize(latitudes)).apply();
            sharedPreferences.edit().putString("longitudes", ObjectSerializer.serialize(longitudes)).apply();
            //sharedPreferences.edit().putString("locations", ObjectSerializer.serialize(MainActivity.locations)).apply();

            /*Log.i("size of locations", ((ArrayList<LatLng>)ObjectSerializer.deserialize(sharedPreferences.getString("locations", ObjectSerializer.serialize(new ArrayList<LatLng>())))).size()+"");
            Toast.makeText(this, ((ArrayList<LatLng>)ObjectSerializer.deserialize(sharedPreferences.getString("locations", ObjectSerializer.serialize(new ArrayList<LatLng>())))).size()+"", Toast.LENGTH_SHORT).show();
            */
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "io exception", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "Location Saved", Toast.LENGTH_SHORT).show();
    }

    private void printArray(ArrayList<String> arr) {
        for(int i =0;i<arr.size();i++){
            Log.d("arr", "printArray: " + arr.get(i));
        }
    }
}
