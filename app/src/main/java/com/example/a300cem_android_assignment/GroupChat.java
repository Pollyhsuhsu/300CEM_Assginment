//package com.example.a300cem_android_assignment;
//
//import androidx.appcompat.app.AppCompatActivity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.EditText;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.example.a300cem_android_assignment.models.ModelChatroom;
//import com.example.a300cem_android_assignment.Adapter.MemberData;
//import com.example.a300cem_android_assignment.Adapter.Message;
//import com.example.a300cem_android_assignment.Adapter.MessageAdapter;
//import com.example.a300cem_android_assignment.Adapter.UserAdapter;
//
//import com.google.firebase.database.ChildEventListener;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//
//import java.util.Calendar;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.text.SimpleDateFormat;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Random;
//
//public class GroupChat extends AppCompatActivity {
//    private UserAdapter currentUser;
//    private ModelChatroom currentChatroom;
//    private MessageAdapter messageAdapter;
//    private ListView messagesView;
//    private MemberData memberData;
//
//    EditText editText;
//    TextView displayTheirMessage;
//    View avatar;
//
//    FirebaseDatabase FireBaseDatabase;
//    DatabaseReference GroupNameRef, GroupMessageKeyRef;
//
//    int currentUserID, currentGroupID;
//    Context context;
//    String currentGroupName, currentGroupDesc, currentCreatedAt, currentData, currentTime;
//    int u_id;
//    String username;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        setContentView(R.layout.activity_chat);
//
//        // Get user and current room info
//        GetcurrentUserInfo();
//        GetcurrentChatRoomInfo();
//
//        // Connect firebase
//        //FireBaseDatabase = FirebaseDatabase.getInstance();
//
//        //DatabaseReference myRef = database.getReference("message");
//
//       // myRef.setValue("Hello, World!");
//
//        messageAdapter = new MessageAdapter(this);
//        messagesView = (ListView) findViewById(R.id.messages_view);
//        messagesView.setAdapter(messageAdapter);
//
//
//        // Hook
//        editText = findViewById(R.id.editText);
//        displayTheirMessage = findViewById(R.id.message_body);
//        avatar = findViewById(R.id.avatar);
//
//        GroupNameRef = FireBaseDatabase.getInstance().getReference().child("Groups").child(Integer.toString(1));
//    }
//
//
//    @Override
//    protected void onStart() {
//        super.onStart();
//
//        GroupNameRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.exists()) {
//                    DisplayMessage(dataSnapshot);
//                }
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                if (dataSnapshot.exists()) {
//                    DisplayMessage(dataSnapshot);
//                }
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }
//
//    private void GetcurrentUserInfo(){
//        Intent intent = getIntent();
//        currentUser = (UserAdapter)intent.getSerializableExtra("currentUser");
//    }
//
//    public void GetcurrentChatRoomInfo(){
//        CallApi callApi = new CallApi();
//        callApi.json_get(new CallApi.VolleyCallback() {
//            @Override
//            public void onSuccessResponse(JSONObject response) throws JSONException {
//                SetcurrentChatRoomInfo(response.getJSONObject("data"));
//            }
//        },"/chatrooms/querybyId/1");
//    }
//
////    public void getMemberData(int id){
////        CallApi callApi = new CallApi();
////        callApi.json_get(new CallApi.VolleyCallback() {
////            @Override
////            public void onSuccessResponse(JSONObject response) throws JSONException {
////                getMemberInfo(response.getJSONObject("data"));
////            }
////        },"/customers/querybyId/" + id);
////    }
////
////    public void getMemberInfo(JSONObject result) throws JSONException {
////        u_id = result.getInt("id");
////        username = result.getString("username");
////        memberData = new MemberData(username,u_id);
////       // memberData.setColor(getRandomColor());
////
////    }
//    private void SetcurrentChatRoomInfo(JSONObject result) throws JSONException {
//        currentGroupID = result.getInt("chatroom_id");
//        currentGroupName = result.getString("chatroom_name");
//        currentGroupDesc = result.getString("chatroom_desc");
//        currentCreatedAt = result.getString("created_at");
//
//        //currentChatroom = new ModelChatroom(currentGroupID,currentGroupName,currentGroupDesc,currentCreatedAt);
//        //GroupNameRef = database.getReference().child("Groups").child(Integer.toString(currentChatroom.getChatroom_id()));
//        GroupNameRef = FireBaseDatabase.getInstance().getReference().child("Groups").child(Integer.toString(currentChatroom.getChatroom_id()));
//        //groupRef.setValue("Hello, World!");
//        System.out.println(currentChatroom.toString());
//    }
//
//
//    private void DisplayMessage(DataSnapshot dataSnapshot){
//        //LayoutInflater messageInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
//        Iterator iterator = dataSnapshot.getChildren().iterator();
//
//        while(iterator.hasNext()){
//
//            boolean belongsToCurrentUser;
//            String chatDate = (String) ((DataSnapshot)iterator.next()).getValue();
//            Long memberid = (Long) ((DataSnapshot)iterator.next()).getValue();
//            String membername = (String) ((DataSnapshot)iterator.next()).getValue();
//            String chatMessage = (String) ((DataSnapshot)iterator.next()).getValue();
//            String chatTime = (String) ((DataSnapshot)iterator.next()).getValue();
//
//            int memberID = memberid.intValue();
//
//            MemberData member = new MemberData(membername,memberID);
//
//            if(memberID == currentUser.getU_id()){
//                belongsToCurrentUser = true;
//            }else{
//                 belongsToCurrentUser = false;
//            }
//
//
//            Message message = new Message(chatMessage, member, belongsToCurrentUser);
//            //displayTextMessages.append();
//        }
//    }
//
//
//    public void sendMessage(View view) {
//        String message = editText.getText().toString();
//        String messageKEY = GroupNameRef.push().getKey();
//
//        if(message != null){
//            Calendar calForDate = Calendar.getInstance();
//            SimpleDateFormat currentDataFormat = new SimpleDateFormat("MMM dd, yyyy");
//            currentData = currentDataFormat.format(calForDate.getTime());
//
//            Calendar calForTime = Calendar.getInstance();
//            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm:ss");
//            currentTime = currentTimeFormat.format(calForTime.getTime());
//
//            HashMap<String, Object> groupMessageKey = new HashMap<>();
//            GroupNameRef.updateChildren(groupMessageKey);
//
//            GroupMessageKeyRef = GroupNameRef.child(messageKEY);
//            HashMap<String, Object> messageInfoMap = new HashMap<>();
//            messageInfoMap.put("memberid", (long)currentUser.getU_id());
//            messageInfoMap.put("membername", currentUser.getUsername());
//            messageInfoMap.put("message", message);
//            messageInfoMap.put("data", currentData);
//            messageInfoMap.put("time", currentTime);
//            GroupMessageKeyRef.updateChildren(messageInfoMap);
//        }
//        if (message.length() > 0) {
//           // scaledrone.publish("observable-room", message);
//            editText.getText().clear();
//        }
//    }
//
//
//    private String getRandomColor() {
//        Random r = new Random();
//        StringBuffer sb = new StringBuffer("#");
//        while(sb.length() < 7){
//            sb.append(Integer.toHexString(r.nextInt()));
//        }
//        return sb.toString().substring(0, 7);
//    }
//}
