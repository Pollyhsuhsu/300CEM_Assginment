package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.a300cem_android_assignment.Adapter.ExistsChatroomListAdapter;
import com.example.a300cem_android_assignment.Adapter.GroupListAdapter;
import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ExistsGroupChatroomList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    static final float END_SCALE = 0.7f;

    private ArrayList<ModelChatroom> groupChatLists = new ArrayList<>();;
    int currentUserID;
    private ListView groupsLv;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_chatroom_ex_list);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);


        //Hook
        groupsLv =(ListView) findViewById(R.id.groupsLv);

        getCurrentUserID();
        naviagtionDrawer();
    }

    private void getCurrentUserID() {
        SessionManagement sessionManagement = new SessionManagement(ExistsGroupChatroomList.this);
        currentUserID = sessionManagement.getSession();

        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                //setCurrentUserInfo(response.getJSONObject("data"));
                loadGroupChatsList(currentUserID);
            }
        },"/customers/querybyId/" + currentUserID);
    }

    private void loadGroupChatsList(int currentUserID){
        if(!groupChatLists.isEmpty()){
            groupChatLists.clear();
        };

        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) throws JSONException {
                if(result.getBoolean("status")) {
                    JSONArray data = result.getJSONArray("data");
                    if(data != null || data.length() > 0) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            int id = obj.getInt("id");
                            String chatroom_name = obj.getString("chatroom_name");
                            String chatroom_desc = obj.getString("chatroom_desc");
                            String chatroom_icon = obj.getString("chatroom_image");
                            int created_by = obj.getInt("created_by");
                            String created_at = obj.getString("created_at");
                            double longitude = obj.getDouble("longitude");
                            double latitude = obj.getDouble("latitude");
                            //double distance = obj.getDouble("distance");

                            ModelChatroom modelChatroom = new ModelChatroom(id, created_by, chatroom_name, chatroom_icon, chatroom_desc, created_at, longitude, latitude, 0);
                            groupChatLists.add(modelChatroom);
                            setGroupChatLists(groupChatLists);
                        }
                    }
                }
            }
        },"/participant/querybyuserid/" + currentUserID);
    }

    private void setGroupChatLists(final ArrayList<ModelChatroom> groupChatLists) {
        ExistsChatroomListAdapter groupListAdapter = new ExistsChatroomListAdapter(ExistsGroupChatroomList.this, R.layout.row_groupchat, groupChatLists);
        if (groupsLv.getAdapter() == null) {
            groupsLv.setAdapter(groupListAdapter);
        } else {
            groupsLv.setAdapter(groupListAdapter);
            groupListAdapter.notifyDataSetChanged();
            groupsLv.invalidateViews();
            groupsLv.refreshDrawableState();
        }
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
                startActivity(intent);
                break;
            case R.id.nav_group_chat:
                break;
            case R.id.Log_out:
                Logout.logout(this);
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}