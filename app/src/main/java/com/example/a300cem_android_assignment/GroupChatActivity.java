package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.example.a300cem_android_assignment.Volley.AppController;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.example.a300cem_android_assignment.Adapter.GroupChatAdapter;
import com.example.a300cem_android_assignment.models.ModelUser;
import com.example.a300cem_android_assignment.models.ModelGroupChat;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class GroupChatActivity extends AppCompatActivity {
    private ModelChatroom currentChatroom;
    private ModelUser currentUser;

    FirebaseDatabase FireBaseDatabase;
    //DatabaseReference GroupNameRef, GroupMessageKeyRef;

    int currentUserID, currentGroupID;
    String currentGroupName, currentGroupDesc, currentCreatedAt, currentData, currentTime;
    String username;

    private Toolbar toolbar;
    private ImageView groupIconIv;
    private ImageButton attachBtn,sendBtn;
    private TextView groupTitleTv;
    private EditText messageEt;
    private RecyclerView chatRv;

    private ArrayList<ModelGroupChat> groupChatList;
    private GroupChatAdapter groupChatAdapter;

    //permission requires constants
    private static final int CAMERA_REQUEST_CODE = 200;
    private static final int STORAGE_REQUEST_CODE = 400;

    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 1000;
    private static final int IMAGE_PICK_GALLERY_CODE = 2000;

    //permissions to be requested

    //pick image uri
    private Uri image_url = null;
    private String[] cameraPermissions;
    private String[] storagePermissions;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //Hook
        toolbar =(Toolbar)  findViewById(R.id.toolbar);
        groupIconIv = (ImageView) findViewById(R.id.groupIconIv);
        groupTitleTv =(TextView)  findViewById(R.id.groupTitleTv);
        attachBtn =(ImageButton)  findViewById(R.id.attacBtn);
        messageEt =(EditText)  findViewById(R.id.messageEt);
        sendBtn =(ImageButton)  findViewById(R.id.sendBtn);
        chatRv =(RecyclerView)  findViewById(R.id.chatRv);

        //Get info.
        getcurrentChatRoomInfo();
        getCurrentUserID();

        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //load message
        loadGroupMessage(currentChatroom);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEt.getText().toString().trim();
                if(TextUtils.isEmpty(message)){
                    //empty, don't send
                    Toast.makeText(GroupChatActivity.this,"Can't send Empty message...", Toast.LENGTH_SHORT).show();
                }else{
                    sendMessage(message);
                }
            }
        });

        attachBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //pick image from camera/gallery
                showImageImportDialog();
            }
        });
    }

    private void showImageImportDialog() {
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
                            //camera clicked
                            if(!checkCameraPermissions()){
                                //not granted,request
                                requestCameraPermissions();
                            }else{
                                pickCamera();
                            }
                        }else{
                            if(!checkStoragePermissions()){
                                requestStoragePermissions();
                            }else{
                                pickGallery();
                            }
                        }
                    }
                }).show();
    }

    private void pickGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickCamera(){
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Group Image Title:");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Description");
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

    private void loadGroupMessage(final ModelChatroom cc) {
        System.out.println(cc.toString());
        //init list
        groupChatList = new ArrayList<>();

        DatabaseReference ref = FireBaseDatabase.getInstance().getReference("Groups");
        ref.child(Integer.toString(cc.getChatroom_id())).child("Messages")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        groupChatList.clear();
                        for(DataSnapshot ds: dataSnapshot.getChildren()){
                            ModelGroupChat model = ds.getValue(ModelGroupChat.class);
                            System.out.println("mode"+ model.toString());
                            groupChatList.add(model);
                        }

                        //adapter
                        groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this,groupChatList,currentUserID,cc.getChatroom_id());
                        //set to recyclerview
                        chatRv.setAdapter(groupChatAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getCurrentUserID() {
        SessionManagement sessionManagement = new SessionManagement(GroupChatActivity.this);
        currentUserID = sessionManagement.getSession();
        Log.d("userID", String.valueOf(currentUserID));

        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                setCurrentUserInfo(response.getJSONObject("data"));
            }
        },"/customers/querybyId/" + currentUserID);
    }

    private void setCurrentUserInfo(JSONObject result) throws JSONException {
        username = result.getString("username");
    }

    private void sendMessage(final String message) {
        // timestamp
        String timestamp = "" + System.currentTimeMillis();

        // setup message data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender_id", (long)currentUserID);
        hashMap.put("sender_name", username);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);
        hashMap.put("type", "" + "text"); // text/image/file

        DatabaseReference GroupNameRef = FireBaseDatabase.getInstance().getReference().child("Groups");
        GroupNameRef.child(Integer.toString(currentChatroom.getChatroom_id())).child("Messages").child(timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //message sent
                        //clear messageEt
                        messageEt.setText("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GroupChatActivity.this,"" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        dbcheckifentryExistofNot();
    }

    private void dbcheckifentryExistofNot(){
        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) throws JSONException {
                //groupChatLists = new ArrayList<>();
                if(!result.getBoolean("exists")) {
                    fbcheckifentryExistofNot();
                }
            }
        },"/participant/checkexists/" + currentUserID + "&" + currentChatroom.getChatroom_id());
    }

    private void fbcheckifentryExistofNot(){
        DatabaseReference GroupNameRef = FireBaseDatabase.getInstance().getReference().child("Groups");
        GroupNameRef.child(Integer.toString(currentChatroom.getChatroom_id())).child("Messages")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ArrayList<Integer> arrayList = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            ModelGroupChat model = ds.getValue(ModelGroupChat.class);
                            arrayList.add(model.getSender_id());
                        }
                        boolean ans = arrayList.contains(currentUserID);

                        if(ans){
                            joinChatroom();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void joinChatroom(){
        CallApi callApi = new CallApi();
        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("user_id", currentUserID);
            jsonBodyObj.put("chatroom_id", currentChatroom.getChatroom_id());
        }catch (JSONException e){
            e.printStackTrace();
        }
        callApi.json_post(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                if(response.getBoolean("status")){

                }
            }
        },"/participant/join",jsonBodyObj);
    }

    private void getcurrentChatRoomInfo() {

        /*
        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                SetcurrentChatRoomInfo(response.getJSONObject("data"));
            }
        },"/chatrooms/querybyId/1");
        */

        Intent intent = getIntent();
        currentChatroom = (ModelChatroom) intent.getSerializableExtra("currentChatroom");
        groupTitleTv.setText(currentChatroom.getChartroom_name());
        stringtoImage(currentChatroom.getChatroom_icon());
        loadGroupMessage(currentChatroom);
    }

    private void SetcurrentChatRoomInfo(JSONObject result) throws JSONException {

        currentGroupID = result.getInt("chatroom_id");
        currentGroupName = result.getString("chatroom_name");
        currentGroupDesc = result.getString("chatroom_desc");
        currentCreatedAt = result.getString("created_at");
        //currentChatroom = new ModelChatroom(currentGroupID, currentGroupName, currentGroupDesc, currentCreatedAt);
        //GroupNameRef = database.getReference().child("Groups").child(Integer.toString(currentChatroom.getChatroom_id()));


        //groupRef.setValue("Hello, World!");
        System.out.println(currentChatroom.toString());

        groupTitleTv.setText(currentGroupName);

        loadGroupMessage(currentChatroom);
    }

    private String getRandomColor() {
        Random r = new Random();
        StringBuffer sb = new StringBuffer("#");
        while(sb.length() < 7){
            sb.append(Integer.toHexString(r.nextInt()));
        }
        return sb.toString().substring(0, 7);
    }

    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupChatActivity.this);

        builder.setTitle("Please confirm");
        builder.setMessage("Are you want to exit the chat room?");
        builder.setCancelable(true);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when user want to exit the app
                // Let allow the system to handle the event, such as exit the app
                GroupChatActivity.super.onBackPressed();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Do something when want to stay in the app
                //Toast.makeText(GroupChatActivity.this,"thank you",Toast.LENGTH_LONG).show();
            }
        });

        // Create the alert dialog using alert dialog builder
        AlertDialog dialog = builder.create();

        // Finally, display the dialog when user press back button
        dialog.show();
    }

    private void stringtoImage(String images){
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
                groupIconIv.setImageResource(R.drawable.ic_group_primary);
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                groupIconIv.setImageBitmap(response.getBitmap());
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
                sendImageMessage();

            }else if(requestCode == IMAGE_PICK_CAMERA_CODE){
                //was picked from camera
                //set to imageview
                sendImageMessage();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImageMessage() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setTitle("Please wait");
        pd.setMessage("Sending Image...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        //file name and path in firebase
        String filenamePath = "ChatImages/"+ "" + System.currentTimeMillis();
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(filenamePath);
        //upload image

        storageReference.putFile(image_url)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //image uploaded, get uri
                        Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while(!p_uriTask.isSuccessful());
                        Uri p_downloadUri = p_uriTask.getResult();

                        if(p_uriTask.isSuccessful()){
                            //image url received, save in db
                            // timestamp
                            String timestamp = "" + System.currentTimeMillis();

                            // setup message data
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("sender_id", (long)currentUserID);
                            hashMap.put("sender_name", username);
                            hashMap.put("message", p_downloadUri);
                            hashMap.put("timestamp", timestamp);
                            hashMap.put("type", "" + "image"); // text/image/file

                            DatabaseReference GroupNameRef = FireBaseDatabase.getInstance().getReference().child("Groups");
                            GroupNameRef.child(Integer.toString(currentChatroom.getChatroom_id())).child("Messages").child(timestamp)
                                    .setValue(hashMap)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            //message sent
                                            //clear messageEt
                                            messageEt.setText("");
                                            pd.dismiss();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            pd.dismiss();
                                            Toast.makeText(GroupChatActivity.this,"" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            dbcheckifentryExistofNot();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //failed uploading image
                        Toast.makeText(GroupChatActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if(grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if(cameraAccepted && storageAccepted){
                        pickCamera();
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
                        pickGallery();
                    }else{
                        Toast.makeText(this,"Storage permissions are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
