package com.ritorno.geoblocks;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.ritorno.geoblocks.directionshelpers.FetchURL;
import com.ritorno.geoblocks.directionshelpers.TaskLoadedCallback;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class MapActivity extends FragmentActivity implements LocationListener, GoogleMap.OnMarkerClickListener, Runnable, TaskLoadedCallback, OnMapReadyCallback {
    public GoogleMap map;
    public LocationManager lm;
    public Criteria criteria;
    public String provider;
    private SupportMapFragment frag;
    boolean permission_cam = false;
    private final int CAMERA_PERMISSION = 1;
    private final int LOCATION_PERMISSION = 2;
    public Polyline currentPolyline;
    public Marker targetPkmn;
    public int Reuest_Lat_Long = 5000;
    public int Distance_in_Metres = 0;

    public List<Poke_Location> appearances;
    public Map<Marker, Poke_Location> appearanceMap;
    public int intervalBetweenSweepstakesInMinutes = 1;
    public double minimumdistanceToBattle = 150.0;
    public Marker eu;
    public ProgressDialog pb;
    public boolean firstposition = true;
    public boolean continueSearch = true;
    public final static int MENU_Profile = 1;
    public final static int Menu_MAp = 2;
    public final static int MENU_POKEDEX = 3;
    public final static int MENU_Eggs = 4;
    public Marker LatMin;
    public Marker LatMax;
    public Marker LongMin;
    public Marker LongMax;
    public ImageButton troca;
    public Location current_position, position_init;
    public TextView tv;
    public SharedPreferences sp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        sp = this.getSharedPreferences("wallet", MODE_PRIVATE);
        frag = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        frag.getMapAsync(this);
        troca = (ImageButton) findViewById(R.id.button_trade);
        targetPkmn = null;
        tv = this.findViewById(R.id.wallet_adress);

        String walletadress = sp.getString("address", "");

        tv.setText(walletadress);

        appearances = new ArrayList<Poke_Location>();
        appearanceMap = new HashMap<Marker, Poke_Location>();
        pb = new ProgressDialog(this);
        pb.setTitle("Loading");

        ImageButton Profile_img = (ImageButton) findViewById(R.id.button_profile);
        Profile_img.setImageResource(R.drawable.male_profile);

        PackageManager packageManager = getPackageManager();
        boolean hasCam = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        if (hasCam)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission();
        } else {
            ConfigureCurrentLocation();
            StartGeoLocation(this);
            if(map != null)
                map.setMyLocationEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {

        }catch (Exception e){
            Log.e("RESUME", "ERROR: " + e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        lm.removeUpdates(this);
        Log.d("Provider", "Provider " + provider + " stopped!");

        continueSearch = false;

        try {
        }catch (Exception e){
            Log.e("Map", "ERROR: " + e.getMessage());
        }

        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        current_position = location;

        LatLng character = new LatLng(location.getLatitude(),location.getLongitude());

        if(eu != null) {
            eu.remove();
        }

        if(targetPkmn != null){
            double distanciaPkmn = getDistancePkmn(eu, targetPkmn);
            double distanciaMin = minimumdistanceToBattle;
            if(distanciaPkmn <= distanciaMin){
                Toast.makeText(this,"You are already close to" + targetPkmn.getTitle() + "!\n" +
                        "Try to capture it now! ", Toast.LENGTH_LONG).show();
            }
        }

        if(map != null) {
                eu = map.addMarker(new MarkerOptions().position(character).icon(BitmapDescriptorFactory.fromResource(R.drawable.male)));
        }

        if(firstposition) {

            CameraUpdate c = CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(character)
                            .tilt(60)
                            .zoom(18)
                            .build());

            map.animateCamera(c);
            firstposition = false;
            position_init = location;

            new Thread(this).start();
        }
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

    public void ConfigureCurrentLocation() {
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();

        PackageManager packageManager = getPackageManager();
        boolean hasGPS = packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS);

        if (hasGPS) {
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            Log.i("LOCATION", "using GPS");
        } else {
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);
            Log.i("LOCATION", "using WI-FI ou data");
        }
    }

    @SuppressLint("MissingPermission")
    public void StartGeoLocation(Context ctx) {

        provider = lm.getBestProvider(criteria, true);

        if (provider == null) {
            Log.e("provider", "No providers found");
        } else {
            Log.i("provider", "The provider is being used" + provider);

            lm.requestLocationUpdates(provider, Reuest_Lat_Long, Distance_in_Metres, (LocationListener) ctx);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String tag = "";
        if (marker.getTag() != null)
            tag = marker.getTag().toString();

        if (tag == "pokemon") {
            if (!marker.equals(eu)) {
                double distancePkmn = getDistancePkmn(eu, marker);
                double distanceMin = minimumdistanceToBattle;

                if (distancePkmn <= distanceMin) {
                    try {

                        Intent it = new Intent(this, CapturaActivity.class);
                        startActivity(it);

                        if (marker.equals(targetPkmn)) {
                            if (currentPolyline != null)
                                currentPolyline.remove();
                            targetPkmn = null;
                        }
                        marker.remove();
                    } catch (Exception e) {
                        Log.e("CliqueMarker", "Erro: " + e.getMessage());
                    }
                } else {
                    if (marker.equals(targetPkmn)) {
                        if (currentPolyline != null)
                            currentPolyline.remove();
                        targetPkmn = null;
                    } else {
                        targetPkmn = marker;
                        String url = getDirectionsUrl(eu.getPosition(), marker.getPosition());
                        new FetchURL(MapActivity.this).execute(url);
                        DecimalFormat df = new DecimalFormat("0.##");
                        Toast.makeText(this, "You are" + df.format(distancePkmn) + " metres from " + marker.getTitle() + ".\n" +
                                "At least get closer " + df.format(distancePkmn - distanceMin) + " metres!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
        return false;
    }


    public void requestLocationPermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {

            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , LOCATION_PERMISSION
            );

            Toast.makeText(this
                    , "Allow access to the device's location to\n" +
                            "measure the distance to the selected location."
                    , Toast.LENGTH_LONG).show();

        } else {
            ActivityCompat.requestPermissions(this
                    , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}
                    , LOCATION_PERMISSION
            );
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permission_cam = true;
                    Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
                } else {
                    permission_cam = true;
                    Toast.makeText(this, "Permission Required to use the camera", Toast.LENGTH_LONG).show();
                }
            }
            case LOCATION_PERMISSION: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ConfigureCurrentLocation();
                    StartGeoLocation(this);
                    if (map != null)
                        map.setMyLocationEnabled(true);
                }
            }
        }
    }
    private String getDirectionsUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String mode = "mode=walking";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + "AIzaSyBjrl4THnAIPgmprHI4IOVCzuFk_qZ0rPQ";
        return url;
    }

    public void clearMarkers(){
        try{
            for (Map.Entry<Marker, Poke_Location> entry : appearanceMap.entrySet()){
                entry.getKey().remove();
            }

            appearanceMap.clear();
            currentPolyline.remove();
            targetPkmn = null;
        }catch (Exception e){
            Log.e("LimparMarker","ERRO: " + e.getMessage());
        }
    }


    public void plotMarkers() {


        ArrayList<Poke_Location> locations = new ArrayList<>();

        int radius = 300;
        LatLng point = new LatLng(current_position.getLatitude(), current_position.getLongitude());
        Location myLocation = new Location("");
        myLocation.setLatitude(point.latitude);
        myLocation.setLongitude(point.longitude);

        for(int i = 0; i<10; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;
            Random random = new Random();
            double radiusInDegrees = radius / 111000f;
            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);
            double new_x = x / Math.cos(y0);
            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            locations.add( new Poke_Location(randomLatLng.latitude, randomLatLng.longitude, "Silver Box") );
        }

        radius = 1000;


        for(int i = 0; i<20; i++) {
            double x0 = point.latitude;
            double y0 = point.longitude;
            Random random = new Random();
            double radiusInDegrees = radius / 111000f;
            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);
            double new_x = x / Math.cos(y0);
            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            locations.add( new Poke_Location(randomLatLng.latitude, randomLatLng.longitude, "Golden Box") );
        }

        try {


            for(int i = 0; i < locations.size(); i++){

                if(locations.get(i).getName().equals("Golden Box"))
                {
                    Marker pokePonto = map.addMarker(new MarkerOptions().
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.gold_32)).
                            position(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude())).
                            title(locations.get(i).getName()));
                    pokePonto.setTag("pokemon");
                    appearanceMap.put(pokePonto, locations.get(i));
                }else {
                    Marker pokePonto = map.addMarker(new MarkerOptions().
                            icon(BitmapDescriptorFactory.fromResource(R.drawable.silver_32)).
                            position(new LatLng(locations.get(i).getLatitude(), locations.get(i).getLongitude())).
                            title(locations.get(i).getName()));
                    pokePonto.setTag("pokemon");
                    appearanceMap.put(pokePonto, locations.get(i));
                }

            }


        }catch (Exception e){
        }
    }



    public double getDistancePkmn(Marker treinador, Marker pkmn){

        Location trainer = new Location(provider);
        trainer.setLatitude(treinador.getPosition().latitude);
        trainer.setLongitude(treinador.getPosition().longitude);

        Location poke = new Location(provider);
        poke.setLatitude(pkmn.getPosition().latitude);
        poke.setLongitude(pkmn.getPosition().longitude);

        return trainer.distanceTo(poke);
    }

    public double getDistancePkStop(Marker treinador, Location pkStop){

        Location trainer = new Location(provider);
        trainer.setLatitude(treinador.getPosition().latitude);
        trainer.setLongitude(treinador.getPosition().longitude);

        return trainer.distanceTo(pkStop);
    }

    public void calculateLatLongMinMaxForDraw(Location location){

            double kmInLongitudeDegree = 111.320 * Math.cos( location.getLatitude() / 180.0 * Math.PI);
            double radiusInKm = 0.3;
            double deltaLat = radiusInKm / 111.1;
            double deltaLong = radiusInKm / kmInLongitudeDegree;

            double minLat = location.getLatitude() - deltaLat;
            double maxLat = location.getLatitude() + deltaLat;
            double minLong = location.getLongitude() - deltaLong;
            double maxLong = location.getLongitude() + deltaLong;


            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clearMarkers();
                    plotMarkers();
                }
            });


    }

    public void plotarMarcadorLatLongMinMax(Location location, double minLat, double maxLat, double minLong, double maxLong){
        if(LatMin != null && LatMax != null && LongMin != null && LongMax != null){
            LatMin.remove();
            LatMax.remove();
            LongMin.remove();
            LongMax.remove();
        }
        LatMin = map.addMarker(new MarkerOptions().position(new LatLng(minLat, location.getLongitude())).title("LatMin"));
        LatMax = map.addMarker(new MarkerOptions().position(new LatLng(maxLat, location.getLongitude())).title("LatMax"));
        LongMin = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), minLong)).title("LongMin"));
        LongMax = map.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), maxLong)).title("LongMax"));
    }

    public void clickPokedex(View v){

    }

    public void clickProfile(View v){

    }

    public void clickTroca(View v) {

    }

    @Override
    public void run() {
        try {
            while (continueSearch) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.show();
                    }
                });

                TimeUnit.SECONDS.sleep(3);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb.dismiss();
                    }
                });

                calculateLatLongMinMaxForDraw(current_position);
                TimeUnit.MINUTES.sleep(intervalBetweenSweepstakesInMinutes);
            }
        }catch (Exception e){

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == MENU_Profile && resultCode == MENU_Profile){

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            map.setMyLocationEnabled(true);
        map.setBuildingsEnabled(false);
        map.setOnMarkerClickListener(this);
        map.setMyLocationEnabled(false);
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.styles));
    }

    @Override
    public void onTaskDone(Object... values) {
        if(currentPolyline!=null) {
            currentPolyline.remove();
        }
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
