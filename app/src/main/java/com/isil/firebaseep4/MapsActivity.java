package com.isil.firebaseep4;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.isil.firebaseep4.databinding.ActivityMapsBinding;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener, View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FusedLocationProviderClient fusedLocationClient;
    Button mbutton;
    EditText mlatitud;
    EditText mlongitud;
    EditText mlugar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mbutton = findViewById(R.id.btnEnviar);
        mbutton.setOnClickListener(this);

        mlatitud = findViewById(R.id.etLatitud);
        mlongitud = findViewById(R.id.etLongitud);
        mlugar = findViewById(R.id.etLugar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        obtenerUbicacionActual();

        mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));
        // Add a marker in Sydney and move the camera
     }

    private void obtenerUbicacionActual() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            solicitarPermisos();
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {

                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            // Logic to handle location object

                            cambiarLugar(location.getLatitude(),location.getLongitude(), "You are here");

                        }
                    }
                });
    }

    private void solicitarPermisos() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION },100);
    }

    private void cambiarLugar(double latitud, double longitud, String text){
        LatLng ubicacion = new LatLng(latitud,longitud);
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(ubicacion)
                .draggable(true)
                .snippet("Marcador")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                .title(text));
        marker.setTag(text);
        mMap.setOnMarkerDragListener(this);
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion,18));
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(ubicacion)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        dibujarCirculo(latitud, longitud, text);

    }

    private void mostrarUbicaciones() {
        double latitud = Double.parseDouble(mlatitud.getText().toString());
        double longitud = Double.parseDouble(mlongitud.getText().toString());
        String lugar = (mlugar.getText().toString());
        Log.d("POS", String.valueOf(longitud));
        cambiarLugar(latitud,longitud, lugar);

    }

    private void dibujarCirculo(Double latitud, Double longitud, String text) {
        // Instantiates a new CircleOptions object and defines the center and radius
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(latitud, longitud))
                .radius(100)
                .fillColor(Color.argb(50,255,0,0));

        // Get back the mutable Circle
        Circle circle = mMap.addCircle(circleOptions);

        circle.setClickable(true);
        circle.setTag(text);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnEnviar:
                mostrarUbicaciones();
                break;
        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {

    }
}