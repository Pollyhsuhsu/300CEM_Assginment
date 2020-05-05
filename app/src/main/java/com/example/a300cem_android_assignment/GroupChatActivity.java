package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

        getcurrentChatRoomInfo();
        getCurrentUserID();
 //       loodGroupInfo();
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

}
