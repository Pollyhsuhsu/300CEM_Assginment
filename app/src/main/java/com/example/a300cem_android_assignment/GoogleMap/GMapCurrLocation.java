package com.example.a300cem_android_assignment.GoogleMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.a300cem_android_assignment.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Locale;

public class GMapCurrLocation extends AppCompatActivity{
    private ImageView map_return;
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;
    private static final int REQUEST_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_g_map_curr_location);
        map_return = findViewById(R.id.map_return);
        //Assign Variable
        supportMapFragment =(SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.google_map);
        //Initialize fused location
        client = LocationServices.getFusedLocationProviderClient(this);

        if(ActivityCompat.checkSelfPermission(GMapCurrLocation.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getCurrenLation();
        }else{
            ActivityCompat.requestPermissions(GMapCurrLocation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getCurrenLation() {
        Task<Location>task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if(location != null){
                    // Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            // Initialize lat lng
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            // Create marker options
                            MarkerOptions options = new MarkerOptions().position(latLng).title(getCompleteAddress(location.getLatitude(),location.getLongitude()));
                            // Zoom map
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,110));
                            // Add market on map
                            googleMap.addMarker(options);

                            map_return.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    GMapCurrLocation.this.finish();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private String getCompleteAddress(double Latitude, double Longitude){
        String address = "";
        Geocoder geocoder = new Geocoder(GMapCurrLocation.this, Locale.getDefault());

        try{
            List<Address> addresses = geocoder.getFromLocation(Latitude, Longitude,1);

            if(addresses != null){
                Address returnAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");
                for(int i = 0; i<= returnAddress.getMaxAddressLineIndex(); i++){
                    strReturnedAddress.append(returnAddress.getAddressLine(i)).append("\n");
                }
                address = strReturnedAddress.toString();
            }else{
                Toast.makeText(this,"Address not found",Toast.LENGTH_SHORT).show();;
            }
        }catch (Exception e){
            Toast.makeText(this,e.getMessage().toString(),Toast.LENGTH_SHORT).show();;
        }
        return address;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 44){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrenLation();
            }
        }
    }
}
