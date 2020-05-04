package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.a300cem_android_assignment.GoogleMap.GMapCurrLocation;
import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.example.a300cem_android_assignment.models.ModelUser;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class CreateChatRoom extends AppCompatActivity{

    static final float END_SCALE = 0.7f;

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int LOCATION_REQUEST_CODE = 101;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;

    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;

    //pick image uri
    private Uri image_url = null;

    //location
    double latitude;
    double longitude;

    private ImageView groupIconIv;
    private EditText groupTitleEt, groupDescriptionEt;
    private FloatingActionButton createGroupBtn;
    private ImageView returnPage;
    private ModelUser currentUser;
    private TextView mlocation;
    Bitmap bitmap;
    private FusedLocationProviderClient fusedLocationClient;
    //
    int userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_create_chat_room);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //get current user information
        getCurrentUserInfo();
        //Hook
        groupIconIv = findViewById(R.id.groupIconIv);
        groupTitleEt = findViewById(R.id.groupTitleEt);
        groupDescriptionEt = findViewById(R.id.groupDescriptionEt);
        createGroupBtn = findViewById(R.id.createGroupBtn);
        returnPage = findViewById(R.id.returnPage);
        mlocation = findViewById(R.id.location);
        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        groupIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });
        returnPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateChatRoom.this, GroupChatroomList.class);
                startActivity(intent);
            }
        });
        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCreatingGroup();
            }
        });
        mlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateChatRoom.this, GMapCurrLocation.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ActivityCompat.checkSelfPermission(CreateChatRoom.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getCurrentLocation();
        }else{
            ActivityCompat.requestPermissions(CreateChatRoom.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }
    }
    private void getCurrentUserInfo() {
        SessionManagement sessionManagement = new SessionManagement(CreateChatRoom.this);
        userID = sessionManagement.getSession();
    }

    private void startCreatingGroup() {
        boolean image;
        final String groupTitle = groupTitleEt.getText().toString().trim();
        final String groupDescription = groupDescriptionEt.getText().toString().trim();

        if(TextUtils.isEmpty(groupTitle)){
            Toast.makeText(this,"Please enter group title...", Toast.LENGTH_SHORT).show();
            return;
        }

        //timestamp: for groupicon image, groupid, timeCreated etc
        if(image_url == null){
            //creating group without icon image
            image = false;
            createGroup(groupTitle,groupDescription,"",image);
        }else{
            image = true;
            String images = imageToString(bitmap);
            createGroup(groupTitle,groupDescription,images,image);
        }
    }

    private void createGroup(String groupTitle,String groupDescription, String groupIcon,boolean image) {
        CallApi callApi = new CallApi();
        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("chatroom_name", groupTitle);
            jsonBodyObj.put("chatroom_desc", groupDescription);
            jsonBodyObj.put("chatroom_image", groupIcon);
            jsonBodyObj.put("created_by", userID);
            jsonBodyObj.put("booleanImage", image);
            jsonBodyObj.put("latitude", latitude);
            jsonBodyObj.put("longitude", longitude);
        }catch (JSONException e){
            e.printStackTrace();
        }

        callApi.json_post(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                if(response.getBoolean("status")){
                    Toast.makeText(CreateChatRoom.this,"Group created successfully", Toast.LENGTH_SHORT).show();
                    ModelChatroom modelChatroom = new ModelChatroom();
                    int insertid = response.getInt("insertid");
                    nextActity(insertid);
                }
            }
        },"/chatrooms/createchatroom",jsonBodyObj);
    }

    private void nextActity(int insertId){
        Log.d("insetyid",insertId+" ");
        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                JSONObject data = response.getJSONObject("data");
                ModelChatroom modelChatroom = new ModelChatroom();
                modelChatroom.setChatroom_id(data.getInt("id"));
                modelChatroom.setCreated_by(data.getInt("created_by"));
                modelChatroom.setChartroom_name(data.getString("chatroom_name"));
                modelChatroom.setChatroom_desc(data.getString("chatroom_desc"));
                modelChatroom.setChatroom_icon(data.getString("chatroom_image"));
                modelChatroom.setCreated_at(data.getString("created_at"));
                modelChatroom.setLatitude(data.getDouble("latitude"));
                modelChatroom.setLongitude(data.getDouble("longitude"));
                Toast.makeText(CreateChatRoom.this,"Group created successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
                intent.putExtra("currentChatroom", modelChatroom);
                startActivity(intent);
            }
        },"/chatrooms/querybyId/" + insertId);
    }
    private void showImagePickDialog() {
        //options to pick image form
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick Image: ")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //handle clicks
                        if(which == 0){
                            if(!checkCameraPermissions()){
                                requestCameraPermissions();
                            }else{
                                pickFormCamera();
                            }
                        }else{
                            if(!checkStoragePermissions()){
                                requestStoragePermissions();
                            }else{
                                pickFormGallery();
                            }
                        }
                    }
                }).show();
    }

    private void pickFormGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFormCamera(){
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Group Image Icon:");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Icon Description");
        image_url = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_url);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermissions(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickFormCamera();
                    }else{
                        Toast.makeText(this,"Camera & Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if(storageAccepted){
                        pickFormGallery();
                    }else{
                        Toast.makeText(this,"Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case LOCATION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    getCurrentLocation();
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void getCurrentLocation() {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();

                            Geocoder geocoder = new Geocoder(CreateChatRoom.this, Locale.getDefault());

                            try {
                                List<Address> addresses = geocoder.getFromLocation(latitude, longitude,1);
                                if(addresses != null){
                                    Address returnAddress = addresses.get(0);
                                    String city = returnAddress.getAddressLine(0);
                                    SpannableString content = new SpannableString(city);
                                    content.setSpan(new UnderlineSpan(), 0, city.length(), 0);
                                    mlocation.setText(content);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //handle image pick result
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_PICK_GALLERY_CODE){
                //was picked from galler
                image_url = data.getData();
                try {
                    InputStream inputStream = getContentResolver().openInputStream(image_url);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    groupIconIv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //was picked from camera
                //set to imageview
                try {
                    InputStream inputStream = getContentResolver().openInputStream(image_url);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    groupIconIv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void StringtoImage(String images){
        byte[] decodedString  = Base64.decode(images, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        groupIconIv.setImageBitmap(decodedImage);
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        startManagingCursor(cursor);
        int column_index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

}
