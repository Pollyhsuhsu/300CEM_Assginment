package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.example.a300cem_android_assignment.Adapter.ChatroomListAdapter;
import com.example.a300cem_android_assignment.Adapter.GroupListAdapter;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.example.a300cem_android_assignment.models.ModelUser;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GroupChatroomList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<ModelChatroom> groupChatLists;
    static final float END_SCALE = 0.7f;
    private ModelUser currentUser;
    private ListView groupsLv;
    private RelativeLayout createGroupChatroom;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_group_chatroom_list);

        //Hook
        groupsLv =(ListView) findViewById(R.id.groupsLv);
        createGroupChatroom = (RelativeLayout) findViewById(R.id.createGroupChatroom);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);

        loadGroupChatsList();
        naviagtionDrawer();
        getCurrentUserInfo();
        createGroupChatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupChatroomList.this, CreateChatRoom.class);
                startActivity(intent);
            }
        });
    }

    private void getCurrentUserInfo() {
        Intent intent = getIntent();
        currentUser = (ModelUser) intent.getSerializableExtra("currentUser");
    }

    private void loadGroupChatsList(){
        groupChatLists = null;
        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) throws JSONException {
                groupChatLists = new ArrayList<>();
                JSONArray data = result.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = data.getJSONObject(i);
                    int id = obj.getInt("id");
                    String chatroom_name = obj.getString("chatroom_name");
                    String chatroom_desc = obj.getString("chatroom_desc");
                    String chatroom_icon = obj.getString("chatroom_image");
                    int created_by =  obj.getInt("created_by");
                    String created_at = obj.getString("created_at");

                    ModelChatroom modelChatroom = new ModelChatroom(id, created_by, chatroom_name, chatroom_icon, chatroom_desc, created_at);
                    groupChatLists.add(modelChatroom);
                    setGroupChatLists(groupChatLists);
                }
            }
        },"/chatrooms/All");
    }

    private void setGroupChatLists(final ArrayList<ModelChatroom> groupChatLists){
        GroupListAdapter groupListAdapter = new GroupListAdapter(this,R.layout.row_groupchat,groupChatLists);
        groupsLv.setAdapter(groupListAdapter);
        groupsLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println(groupChatLists.get(position).getChatroom_id());
                if(groupChatLists!=null) {
                    ModelChatroom modelChatroom = new ModelChatroom();
                    modelChatroom.setChatroom_id(groupChatLists.get(position).getChatroom_id());
                    modelChatroom.setCreated_by(groupChatLists.get(position).getCreated_by());
                    modelChatroom.setChartroom_name(groupChatLists.get(position).getChartroom_name());
                    modelChatroom.setChatroom_desc(groupChatLists.get(position).getChatroom_desc());
                    modelChatroom.setChatroom_icon(groupChatLists.get(position).getChatroom_icon());
                    modelChatroom.setCreated_at(groupChatLists.get(position).getCreated_at());

                    Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
                    intent.putExtra("currentChatroom", modelChatroom);
                    intent.putExtra("currentUser", currentUser);
                    startActivity(intent);
                }
            }
        });
    }

    private void naviagtionDrawer() {
        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_group_chat);

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
                intent.putExtra("currentUser", currentUser);
                startActivity(intent);
                break;
            case R.id.nav_group_chat:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
