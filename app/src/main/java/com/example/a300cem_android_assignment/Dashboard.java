package com.example.a300cem_android_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.a300cem_android_assignment.HomeAdapter.CategoriesAdapter;
import com.example.a300cem_android_assignment.HomeAdapter.CategoriesHelperClass;
import com.example.a300cem_android_assignment.HomeAdapter.FeaturedAdpater;
import com.example.a300cem_android_assignment.HomeAdapter.FeaturedHelperClass;
import com.example.a300cem_android_assignment.HomeAdapter.MostViewedAdpater;
import com.example.a300cem_android_assignment.HomeAdapter.MostViewedHelperClass;
import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.example.a300cem_android_assignment.Volley.AppController;
import com.example.a300cem_android_assignment.models.ModelUser;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ImageView near_by;
    RecyclerView featuredRecycler, mostViewedRecycler, categoriesRecycler;
    RecyclerView.Adapter adapter;

    private GradientDrawable gradient1, gradient2, gradient3, gradient4;
    static final float END_SCALE = 0.7f;

    //Drawer Menu
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ImageView menuIcon;
    LinearLayout contentView;

    int userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_dashboard);

        //Hooks
        featuredRecycler = findViewById(R.id.featured_recycler);
        mostViewedRecycler = findViewById(R.id.mostViewedRecycler);
        categoriesRecycler = findViewById(R.id.categories_recycler);
        near_by = findViewById(R.id.near_by);

        //Menu Hooks
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);

        //Recycler Views Function calls
        featuredRecycler();
        mostViewedRecycler();
        categoriesRecycler();
        naviagtionDrawer();
        getCurrentUserInfo();

        near_by.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Dashboard.this, NyGroupChatroomList.class);
                startActivity(intent);
            }
        });
    }

    private void getCurrentUserInfo() {
        SessionManagement sessionManagement = new SessionManagement(Dashboard.this);
        userID = sessionManagement.getSession();

        //save uid of currently signed in user in share preferences
        SharedPreferences sp = getSharedPreferences("SP_USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("Current_USERID", userID);
        editor.apply();
        Log.d("userID", String.valueOf(userID));
    }

    private void nextActivity(){
        Intent intent = new Intent(this,NyGroupChatroomList.class);
        startActivity(intent);
    }
    private void naviagtionDrawer() {
        //Naviagtion Drawer
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);

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

    private void featuredRecycler() {
        featuredRecycler.setHasFixedSize(true);
        featuredRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));

        ArrayList<FeaturedHelperClass> featuredLocations = new ArrayList<>();

        featuredLocations.add(new FeaturedHelperClass(R.drawable.photo1,"kaukau","kdjfiajsdfijsadiof"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.photo1,"kaukau2","kdjfiajsdfijsadiof"));
        featuredLocations.add(new FeaturedHelperClass(R.drawable.photo1,"kaukau3","kdjfiajsdfijsadiof"));

        adapter = new FeaturedAdpater(featuredLocations);
        featuredRecycler.setAdapter(adapter);
    }

    private void categoriesRecycler() {

        //All Gradients
        gradient2 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffd4cbe5, 0xffd4cbe5});
        gradient1 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xff7adccf, 0xff7adccf});
        gradient3 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xfff7c59f, 0xFFf7c59f});
        gradient4 = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[]{0xffb8d7f5, 0xffb8d7f5});


        ArrayList<CategoriesHelperClass> categoriesHelperClasses = new ArrayList<>();
        categoriesHelperClasses.add(new CategoriesHelperClass( R.drawable.photo1,"Education", gradient1));
        categoriesHelperClasses.add(new CategoriesHelperClass(R.drawable.photo1, "HOSPITAL", gradient2));
        categoriesHelperClasses.add(new CategoriesHelperClass(R.drawable.photo1, "Restaurant", gradient3));
        categoriesHelperClasses.add(new CategoriesHelperClass(R.drawable.photo1, "Shopping", gradient4));
        categoriesHelperClasses.add(new CategoriesHelperClass(R.drawable.photo1, "Transport", gradient1));


        categoriesRecycler.setHasFixedSize(true);
        adapter = new CategoriesAdapter(categoriesHelperClasses);
        categoriesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoriesRecycler.setAdapter(adapter);
    }

    private void mostViewedRecycler() {

        mostViewedRecycler.setHasFixedSize(true);
        mostViewedRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        final ArrayList<MostViewedHelperClass> mostViewedLocations = new ArrayList<>();
        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) throws JSONException {
                if(result.getBoolean("status")) {
                    JSONArray data = result.getJSONArray("data");
                    if(data != null || data.length()>0) {
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject obj = data.getJSONObject(i);
                            String chatroom_name = obj.getString("chatroom_name");
                            String chatroom_desc = obj.getString("chatroom_desc");
                            String chatroom_icon = obj.getString("chatroom_image");
                            mostViewedLocations.add(new MostViewedHelperClass(chatroom_icon, chatroom_name,chatroom_desc));
                        }
                    }
                }
            }
        },"/participant/most");
//        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.photo1, "McDonald's","sdfasdfasdf"));
//        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.photo1, "Edenrobe","sdfasdfasdf"));
//        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.photo1, "J.","sdfasdfasdf"));
//        mostViewedLocations.add(new MostViewedHelperClass(R.drawable.photo1, "Walmart","sdfasdfasdf"));

        adapter = new MostViewedAdpater(mostViewedLocations);
        mostViewedRecycler.setAdapter(adapter);
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
                break;
            case R.id.nav_profile:
                intent = new Intent(this,UserProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_nearby:
//                intent = new Intent(this,NyGroupChatroomList.class);
//                startActivity(intent);
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

}
