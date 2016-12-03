package com.ama.findapplication;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView mLatitudeText;
    private TextView mLongitudeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        // Create an instance of GoogleAPIClient.

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

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
        int a = 40;
        int b = 40;
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(a, b);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPerm  issionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            Toast.makeText(this, ""+String.valueOf(mLastLocation.getLatitude())+"-"+String.valueOf(mLastLocation.getLongitude()), Toast.LENGTH_SHORT).show();
            //zoom to current position:
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())).zoom(14).build(); //
            //gpsin algıladıgi yere gider
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), new GoogleMap.CancelableCallback() {
                @Override
                public void onFinish() {
                    VisibleRegion vr = mMap.getProjection().getVisibleRegion();
                    double left = vr.latLngBounds.southwest.longitude;
                    double top = vr.latLngBounds.northeast.latitude;
                    double right = vr.latLngBounds.northeast.longitude;
                    double bottom = vr.latLngBounds.southwest.latitude;

                    Location middleLeftCornerLocation=new Location("middleLeftCornerLocation");
                    middleLeftCornerLocation.setLatitude( (vr.latLngBounds.northeast.latitude-vr.latLngBounds.southwest.latitude)/2+vr.latLngBounds.southwest.latitude);
                    middleLeftCornerLocation.setLongitude( vr.latLngBounds.southwest.longitude);
//
                    Location center=new Location("center");
                    center.setLatitude(mLastLocation.getLatitude());
                    center.setLongitude( mLastLocation.getLongitude());

                    //merkezden ekranin sol orta kosesine olN distance
                    float dis = center.distanceTo(middleLeftCornerLocation);//calculate distane between middleLeftcorner and center

                    mMap.addCircle(new CircleOptions().center(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude())).radius(dis).strokeColor(Color.rgb(255,0,0)).fillColor(Color.argb(80, 255, 0,0)));

                    mMap.addMarker(new MarkerOptions().position(new LatLng(vr.latLngBounds.northeast.latitude,vr.latLngBounds.southwest.longitude)).title("sol üst"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(vr.latLngBounds.northeast.latitude,vr.latLngBounds.northeast.longitude)).title("sağ üst"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(vr.latLngBounds.southwest.latitude,vr.latLngBounds.northeast.longitude)).title("sağ alt"));
                    mMap.addMarker(new MarkerOptions().position(new LatLng(vr.latLngBounds.southwest.latitude,vr.latLngBounds.southwest.longitude)).title("sol alt"));
                }

                @Override
                public void onCancel() {

                }
            });


            //lokasyoa gitme butonu
            mMap.setMyLocationEnabled(true);

          //  mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
