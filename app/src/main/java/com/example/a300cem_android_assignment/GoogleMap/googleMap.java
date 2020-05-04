//package com.example.a300cem_android_assignment.GoogleMap;
//
//import android.content.pm.PackageManager;
//import android.os.Bundle;
//import android.os.Looper;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//import com.example.a300cem_android_assignment.CallApi;
//import com.example.a300cem_android_assignment.R;
//import com.example.a300cem_android_assignment.models.ModelChatroom;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
//import static android.Manifest.permission.ACCESS_FINE_LOCATION;
//
//public class googleMap extends AppCompatActivity {
//    private ArrayList<ModelChatroom> groupChatLists;
//    private static final int REQUEST_CODE_LOCATION_PERMISSON = 1;
//
//    private ProgressBar progressBar;
//    private TextView textLatLong;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_google_map);
//
//        Button button = findViewById(R.id.getLocation);
//        textLatLong = findViewById(R.id.textLatLong);
//        progressBar = findViewById(R.id.progressBar);
////        button.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if(ContextCompat.checkSelfPermission(
////                        getApplication(), ACCESS_FINE_LOCATION
////                )!=PackageManager.PERMISSION_GRANTED){
////                    ActivityCompat.requestPermissions(
////                            googleMap.this,
////                            new String[]{ACCESS_FINE_LOCATION},
////                            REQUEST_CODE_LOCATION_PERMISSON
////                    );
////                }else{
////                    getCurrentLocation();
////                }
////            }
////        });
//    }
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//        if(ContextCompat.checkSelfPermission(
//                getApplication(), ACCESS_FINE_LOCATION
//        )!=PackageManager.PERMISSION_GRANTED){
//            ActivityCompat.requestPermissions(
//                    googleMap.this,
//                    new String[]{ACCESS_FINE_LOCATION},
//                    REQUEST_CODE_LOCATION_PERMISSON
//            );
//        }else{
//            getCurrentLocation();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if(requestCode == REQUEST_CODE_LOCATION_PERMISSON && grantResults.length>0){
//            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                getCurrentLocation();
//            }else{
//                Toast.makeText(this,"Permission denied:", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void getCurrentLocation() {
//        progressBar.setVisibility(View.VISIBLE);
//
//        final LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setInterval(10000);
//        locationRequest.setFastestInterval(3000);
//        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);
//
//        LocationServices.getFusedLocationProviderClient(googleMap.this)
//                .requestLocationUpdates(locationRequest, new LocationCallback(){
//
//                    @Override
//                    public void onLocationResult(LocationResult locationResult) {
//                        super.onLocationResult(locationResult);
//                        LocationServices.getFusedLocationProviderClient(googleMap.this)
//                                .removeLocationUpdates(this);
//                        if(locationRequest != null && locationResult.getLocations().size() > 0){
//                            int latestLocationIndex = locationResult.getLocations().size() - 1;
//                            double latitude =
//                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
//                            double longitude =
//                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();
//                            Log.d("MAG", "latitude编码：" + latitude + "longitude编码：" + longitude);
//
//                        }
//                    }
//                }, Looper.getMainLooper());
//    }
//
//
//    private void loadGroupChatsList(){
//        groupChatLists = null;
//        CallApi callApi = new CallApi();
//        callApi.json_get(new CallApi.VolleyCallback() {
//            @Override
//            public void onSuccessResponse(JSONObject result) throws JSONException {
//                groupChatLists = new ArrayList<>();
//                JSONArray data = result.getJSONArray("data");
//                for (int i = 0; i < data.length(); i++) {
//                    JSONObject obj = data.getJSONObject(i);
//                    int id = obj.getInt("id");
//                    String chatroom_name = obj.getString("chatroom_name");
//                    String chatroom_desc = obj.getString("chatroom_desc");
//                    String chatroom_icon = obj.getString("chatroom_image");
//                    int created_by =  obj.getInt("created_by");
//                    String created_at = obj.getString("created_at");
//
//                   // ModelChatroom modelChatroom = new ModelChatroom(id, created_by, chatroom_name, chatroom_icon, chatroom_desc, created_at);
//                   // groupChatLists.add(modelChatroom);
//                }
//            }
//        },"/chatrooms/All");
//    }
//
//
//}
