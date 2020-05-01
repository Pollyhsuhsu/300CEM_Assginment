package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

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


    private Toolbar toolbar;
    private ImageView imageView;
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
        imageView = (ImageView) findViewById(R.id.groupIconIv);
        groupTitleTv =(TextView)  findViewById(R.id.groupTitleTv);
        attachBtn =(ImageButton)  findViewById(R.id.attacBtn);
        messageEt =(EditText)  findViewById(R.id.messageEt);
        sendBtn =(ImageButton)  findViewById(R.id.sendBtn);
        chatRv =(RecyclerView)  findViewById(R.id.chatRv);

        getcurrentChatRoomInfo();
        getCurrentUserInfo();
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
                        groupChatAdapter = new GroupChatAdapter(GroupChatActivity.this,groupChatList,currentUser.getU_id(),cc.getChatroom_id());
                        //set to recyclerview
                        chatRv.setAdapter(groupChatAdapter);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private void getCurrentUserInfo() {
        Intent intent = getIntent();
        currentUser = (ModelUser) intent.getSerializableExtra("currentUser");
    }

    private void sendMessage(final String message) {
        // timestamp
        String timestamp = "" + System.currentTimeMillis();

        // setup message data
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender_id", (long)currentUser.getU_id());
        hashMap.put("sender_name", currentUser.getUsername());
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
    }

    private void getcurrentChatRoomInfo() {
//        CallApi callApi = new CallApi();
//        callApi.json_get(new CallApi.VolleyCallback() {
//            @Override
//            public void onSuccessResponse(JSONObject response) throws JSONException {
//                SetcurrentChatRoomInfo(response.getJSONObject("data"));
//            }
//        },"/chatrooms/querybyId/1");

        Intent intent = getIntent();
        currentChatroom = (ModelChatroom) intent.getSerializableExtra("currentChatroom");
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
}
