package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.example.a300cem_android_assignment.Volley.AppController;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.example.a300cem_android_assignment.models.ModelUser;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class UserProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
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

    ImageView userIconIv;
    TextView fullnameLabel, usernameLabel;
    TextInputLayout userId,fullName, email, password;

    ModelUser currentUser = new ModelUser();
    Bitmap bitmap;
    int userID;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;
    Button updateProfile;
    ShimmerFrameLayout profile_image_s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_user_profile);

        //Hooks
        userIconIv = findViewById(R.id.profile_image);
        userId = findViewById(R.id.userid_profile);
        fullName = findViewById(R.id.full_name_profile);
        email = findViewById(R.id.email_profile);
        password = findViewById(R.id.password_profile);
        fullnameLabel = findViewById(R.id.fullname_field);
        usernameLabel = findViewById(R.id.username_field);
        updateProfile = findViewById(R.id.updateProfile);
        profile_image_s = findViewById(R.id.profile_image_s);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);

        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        profile_image_s.startShimmer();
        //show All data
        naviagtionDrawer();
        getCurrentUserID();



        userIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImagePickDialog();
            }
        });

        updateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startUpdateProfile();
            }
        });
    }

    private void startUpdateProfile() {
//        final int userID = currentUser.getU_id();
        final String username_update = fullName.getEditText().getText().toString().trim();
        final String email_update  = email.getEditText().getText().toString().trim();
        final String password_update  = password.getEditText().getText().toString().trim();

        if(TextUtils.isEmpty(username_update) || TextUtils.isEmpty(email_update) || TextUtils.isEmpty(password_update) ){
            Toast.makeText(this,"Please enter the information...", Toast.LENGTH_SHORT).show();
            return;
        }

        //timestamp: for groupicon image, groupid, timeCreated etc
        if(image_url == null){
            //creating group without icon image
            updateProfileDB(username_update,email_update,password_update,"", false);
        }else{
            String images = imageToString(bitmap);
            updateProfileDB(username_update,email_update,password_update,images, true);
        }
    }

    private void updateProfileDB(String username_update,String email_update,String password_update, String userIcon, boolean image) {
        CallApi callApi = new CallApi();
        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("id", currentUser.getU_id());
            jsonBodyObj.put("username", username_update);
            jsonBodyObj.put("user_image", userIcon);
            jsonBodyObj.put("booleanImage", image);
            jsonBodyObj.put("email", email_update);
            jsonBodyObj.put("password", password_update);
        }catch (JSONException e){
            e.printStackTrace();
        }

        callApi.json_put(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                if(response.getBoolean("status")){
                    Toast.makeText(UserProfile.this,"User info update successfully", Toast.LENGTH_SHORT).show();
                    ModelChatroom modelChatroom = new ModelChatroom();
                    getCurrentUserID();
                }
            }
        },"/customers/update",jsonBodyObj);
    }

    private void naviagtionDrawer() {
        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_profile);

        menuIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();
    }

    private void animateNavigationDrawer() {
        //Add any color or remove it to use the default one!
        //To make it transparent use Color.Transparent in side setScrimColor();
        //drawerLayout.setScrimColor(Color.TRANSPARENT);
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                // Scale the View based on current slide offset
                final float diffScaledOffset = slideOffset * (1 - END_SCALE);
                final float offsetScale = 1 - diffScaledOffset;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                // Translate the View, accounting for the scaled width
                final float xOffset = drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaledOffset / 2;
                final float xTranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xTranslation);
            }
        });
    }

    private void getCurrentUserID() {
        SessionManagement sessionManagement = new SessionManagement(UserProfile.this);
        userID = sessionManagement.getSession();

        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                if (response.getBoolean("status")) {
                    JSONObject data = response.getJSONObject("data");
                    currentUser = new ModelUser();
                    currentUser.setU_id(data.getInt("id"));
                    currentUser.setUsername(data.getString("username"));
                    currentUser.setEmail(data.getString("email"));
                    currentUser.setPassword(data.getString("password"));
                    currentUser.setCreated_at(data.getString("created_at"));
                    currentUser.setUpdated_at(data.getString("updated_at"));
                    currentUser.setUser_image(data.getString("user_image"));
                    System.out.println(currentUser.toString());
                    showAllUserData();
                }
            }
        },"/customers/querybyId/" + userID);
    }

    private void showAllUserData() {
        fullnameLabel.setText(currentUser.getUsername());
        String[] date = currentUser.getCreated_at().split("T");
        usernameLabel.setText("Join at " + date[0]);
        userId.getEditText().setText(currentUser.getU_id()+"");
        userId.setEnabled(false);
        fullName.getEditText().setText(currentUser.getUsername());
        email.getEditText().setText(currentUser.getEmail());
        password.getEditText().setText(currentUser.getPassword());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                profile_image_s.stopShimmer();
                profile_image_s.setShimmer(null);

                StringtoImage(currentUser.getUser_image(),userIconIv);
            }
        },1000);

    }
    private void StringtoImage(String images, final ImageView userIconIv){
        Log.d("image", images);

        /*
        Picasso.get().load(Uri.parse(images))
        .error(R.drawable.ic_group_primary)
        .priority(Picasso.Priority.HIGH)
        .noFade()
        .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
        .into(groupIconIv);
        */

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        imageLoader.get(images, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                userIconIv.setImageResource(R.mipmap.ic_corgi_foreground);
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                userIconIv.setImageBitmap(response.getBitmap());
            }
        });

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


    private String imageToString(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }




    private void pickFormGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFormCamera(){
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "User Image Icon:");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "User Image Icon Description");
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
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Intent intent;
        switch(menuItem.getItemId()){
            case R.id.nav_home:
                intent = new Intent(this,Dashboard.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                break;
            case R.id.nav_nearby:
                intent = new Intent(this,NyGroupChatroomList.class);
                startActivity(intent);
                break;
            case R.id.nav_message:
                intent = new Intent(this,ExistsGroupChatroomList.class);
                startActivity(intent);
                break;
            case R.id.Log_out:
                Logout.logout(this);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
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
                    userIconIv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //was picked from camera
                //set to imageview
                try {
                    InputStream inputStream = getContentResolver().openInputStream(image_url);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    userIconIv.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
