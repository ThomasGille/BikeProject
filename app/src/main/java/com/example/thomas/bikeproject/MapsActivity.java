package com.example.thomas.bikeproject;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private double lat;
    private double lng;
    List<Station> mListe;
    ClusterManager mClusterManager;
    private Activity Me;
    GoogleApiClient mGoogleApiClient;
    LatLng mPos;
    Context mContext;
    LatLng pointA=null;
    LatLng pointB=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        this.lat = getIntent().getDoubleExtra("lat", -1);
        this.lng = getIntent().getDoubleExtra("lng", -1);
        this.mListe = (List<Station>) getIntent().getSerializableExtra("liste");
        this.Me = this;
        this.mContext=this.getApplicationContext();

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

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



        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mClusterManager = new ClusterManager<MyItem>(this, mMap);
        googleMap.setOnCameraChangeListener(mClusterManager);
        googleMap.setOnMarkerClickListener(mClusterManager);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {

            @Override
            public boolean onMarkerClick(final Marker marker) {
                LatLng pos = marker.getPosition();
                final Station station = getStationByPos(pos);
                /*
                marker.setSnippet(
                        station.getName().substring(GetRealName(station.getName()))+
                        station.getAvailable_bikes() + " / " + station.getBike_stands() + "bikes available"
                );
                //doesn't display
                */
                AlertDialog.Builder builder = new AlertDialog.Builder(Me);
                builder
                        .setTitle(station.getName())
                        .setMessage(
                                "Status :" + (station.getStatus() + "\n" +
                                        "Available bikes : " + station.getAvailable_bikes() + " / " + station.getBike_stands() + "\n" +
                                        "Available bike stands : " + station.getAvailable_bike_stands() + " / " + station.getBike_stands() + "\n" +
                                        "\n" +
                                        "Address : " + station.getAddress() + "\n" +
                                        "Banking : " + station.isBanking() + "\n" +
                                        "Bonus : " + station.isBonus()
                                ));
                                //.setIcon(android.R.drawable.ic_dialog_alert)
                        if(pointA==null){
                            builder.setPositiveButton("Define as point A!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pointA = new LatLng(station.getmPosition().getLat(), station.getmPosition().getLng());
                                }
                            });
                        }
                        else{
                            builder.setPositiveButton("Define as point B!", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    pointB = new LatLng(station.getmPosition().getLat(), station.getmPosition().getLng());
                                    new ItineraireTask(mContext, mMap,pointA,pointB).execute();
                                }
                            });
                        }

                        builder.setNegativeButton("Bring me there!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onConnected(null); // now we got the user' position
                        try {
                            new ItineraireTask(mContext, mMap, mPos, new LatLng(station.getmPosition().getLat(), station.getmPosition().getLng())).execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                        pointA=pointB=null;
                    }
                });
                builder.show();
                return true;
            }
        });
        // Ajoute les éléments au ClusterManager
        for (Station item : mListe) {
            MyItem mItem = new MyItem(item.getmPosition().getLat(), item.getmPosition().getLng());
            mClusterManager.addItem(mItem);
        }
        LatLng Place = new LatLng(lat, lng);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(Place));


    }

    public int GetRealName(String text) {
        int cpt = 0;
        char c = text.charAt(cpt);
        while (c != '-') {
            cpt++;
            c = text.charAt(cpt);
        }
        return cpt + 2;
        //il faut enlever le '-' et l'espace
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public class MyItem implements ClusterItem {
        private final LatLng mPosition;

        public MyItem(double lat, double lng) {
            mPosition = new LatLng(lat, lng);
        }

        @Override
        public LatLng getPosition() {
            return mPosition;
        }
    }

    public Station getStationByPos(LatLng pos) {
        for (Station station : mListe) {
            if (station.getmPosition().getLat() == pos.latitude && station.getmPosition().getLng() == pos.longitude) {
                return station;
            }
        }
        return null;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mPos=new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
