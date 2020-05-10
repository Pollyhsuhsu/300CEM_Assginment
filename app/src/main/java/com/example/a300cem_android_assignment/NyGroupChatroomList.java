package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.a300cem_android_assignment.Adapter.GroupListAdapter;
import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
//
public class NyGroupChatroomList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private ArrayList<ModelChatroom> groupChatLists = new ArrayList<>();;
    private static final int REQUEST_CODE_LOCATION_PERMISSON = 1;
    private ListView groupsLv;
    private ImageView setting_distance;
    private RelativeLayout createGroupChatroom;
    private int currentUserID;
    private int pick = 0;


    static final float END_SCALE = 0.7f;

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

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        //Hook
        groupsLv =(ListView) findViewById(R.id.groupsLv);
        createGroupChatroom = (RelativeLayout) findViewById(R.id.createGroupChatroom);
        setting_distance = findViewById(R.id.setting_distance);

        naviagtionDrawer();
        getCurrentUserInfo();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            mAuth.signInAnonymously();
        }

        createGroupChatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NyGroupChatroomList.this, CreateChatRoom.class);
                startActivity(intent);
            }
        });

        setting_distance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
  }

    @Override
    protected void onStart() {
        super.onStart();
        if(ContextCompat.checkSelfPermission(
                getApplication(), ACCESS_FINE_LOCATION
        )!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(
                    NyGroupChatroomList.this,
                    new String[]{ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSON
            );
        }else{
            getCurrentLocation(pick);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE_LOCATION_PERMISSON && grantResults.length>0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation(pick);
            }else{
                Toast.makeText(this,"Permission denied:", Toast.LENGTH_SHORT).show();
                Intent intent = null;
                NyGroupChatroomList.this.setResult(RESULT_OK, intent);
                NyGroupChatroomList.this.finish();
            }
        }
    }

    private void getCurrentLocation(final int pick) {
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(locationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.getFusedLocationProviderClient(NyGroupChatroomList.this)
                .requestLocationUpdates(locationRequest, new LocationCallback(){

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(NyGroupChatroomList.this)
                                .removeLocationUpdates(this);
                        if(locationRequest != null && locationResult.getLocations().size() > 0){
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            double latitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLatitude();
                            double longitude =
                                    locationResult.getLocations().get(latestLocationIndex).getLongitude();

                            //loadGroupChatsList(latitude,longitude, pick);
                            loadGroupChatsList(latitude,longitude, pick);
                        }
                    }
                }, Looper.getMainLooper());
    }

    private void getCurrentUserInfo() {
        SessionManagement sessionManagement = new SessionManagement(NyGroupChatroomList.this);
        currentUserID = sessionManagement.getSession();
    }

    private void loadGroupChatsList(double latitude,double longitude, int p){
        if(pick == 0){
            pick = 5000;
        }else{
            pick = p;
        }
        if(!groupChatLists.isEmpty()){
            groupChatLists.clear();
        };

        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) throws JSONException {
                //groupChatLists = new ArrayList<>();
                if(result.getBoolean("status")) {
                    JSONArray data = result.getJSONArray("data");

                    if(data != null || data.length()>0) {
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
                            double distance = obj.getDouble("distance");

                            ModelChatroom modelChatroom = new ModelChatroom(id, created_by, chatroom_name, chatroom_icon, chatroom_desc, created_at, longitude, latitude, distance);
                            groupChatLists.add(modelChatroom);
                        }
                            setGroupChatLists(groupChatLists);

                    }
                }
            }
        },"/nearby/nychatroom/" + latitude + "&" + longitude + "&" + pick);
    }

    private void setGroupChatLists(final ArrayList<ModelChatroom> groupChatLists){
         final GroupListAdapter groupListAdapter = new GroupListAdapter(NyGroupChatroomList.this, R.layout.row_groupchat, groupChatLists);
             if(groupsLv.getAdapter() == null) {
                 groupsLv.setAdapter(groupListAdapter);
             }else {
                 groupsLv.setAdapter(groupListAdapter);
                 groupListAdapter.notifyDataSetChanged();
                 groupsLv.invalidateViews();
                 groupsLv.refreshDrawableState();
             }


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
                    startActivity(intent);
                }
            }
        });
    }

    public void show(){
        final Dialog npDialog = new Dialog(NyGroupChatroomList.this);
        npDialog.setTitle("NumberPicker Example");
        npDialog.setContentView(R.layout.seletor_dialog);
        Button setBtn = (Button)npDialog.findViewById(R.id.setBtn);
        Button cnlBtn = (Button)npDialog.findViewById(R.id.CancelButton_NumberPicker);

        final NumberPicker numberPicker = (NumberPicker)npDialog.findViewById(R.id.numberPicker);

        String mValues[] = { "100km ", "200km ","300km", "400km", "450km","500km"};
        setNubmerPicker(numberPicker,mValues);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener(){
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                // TODO Auto-generated method stub
            }
        });

        setBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub


                switch (numberPicker.getValue()) {
                    case 0:
                        pick = 100;
                        break;
                    case 1:
                        pick = 200;
                        break;
                    case 2:
                        pick = 300;
                        break;
                    case 3:
                        pick = 400;
                        break;
                    case 4:
                        pick = 450;
                        break;
                    case 5:
                        pick = 500;
                        break;
                    default:
                        pick = 5000;
                        break;
                }
                Toast.makeText(NyGroupChatroomList.this, "Selected: " + pick + "km" , Toast.LENGTH_SHORT).show();
                npDialog.dismiss();
                getCurrentLocation(pick);
            }
        });

        cnlBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                npDialog.dismiss();
            }
        });

        npDialog.show();
    }

    private void setNubmerPicker(NumberPicker nubmerPicker,String [] numbers){
        nubmerPicker.setMaxValue(numbers.length-1);
        nubmerPicker.setMinValue(0);
        nubmerPicker.setWrapSelectorWheel(false);
        nubmerPicker.setValue(2);
        nubmerPicker.setDisplayedValues(numbers);
    }

    private void naviagtionDrawer() {
        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_nearby);

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
            case R.id.nav_profile:
                intent = new Intent(this,UserProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_nearby:
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
}
