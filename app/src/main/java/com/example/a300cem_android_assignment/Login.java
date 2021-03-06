package com.example.a300cem_android_assignment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.example.a300cem_android_assignment.Wareable.CountingService;
import com.example.a300cem_android_assignment.models.ModelUser;
import com.example.a300cem_android_assignment.notifications.ReminderBroadcast;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    public static final String TAG = "MYTAG";
    public static final String DEBUG_KEY = "DEBUG_KEY";

    ModelUser currentUser;
    Button callSignUp, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username,password;
    ProgressBar progressBar;
    CallApi callApi = new CallApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        // Hook
        callSignUp = findViewById(R.id.signup_screen);
        image = findViewById(R.id.logo_image);
        logoText = findViewById(R.id.logo_name);
        sloganText = findViewById(R.id.slogan_name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);

        callSignUp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                Pair[] pairs = new Pair[7];
                pairs[0] = new Pair<View,String>(image,"logo_image");
                pairs[1] = new Pair<View,String>(logoText,"logo_text");
                pairs[2] = new Pair<View,String>(sloganText,"logo_desc");
                pairs[3] = new Pair<View,String>(username,"username_tran");
                pairs[4] = new Pair<View,String>(password,"password_tran");
                pairs[5] = new Pair<View,String>(login_btn,"button_tran");
                pairs[6] = new Pair<View,String>(callSignUp,"button_signup_tran");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Login.this, pairs);
                startActivity(intent,options.toBundle());
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkSession();
    }

    private void checkSession() {
        //check if user is logged in
        //if user is logged in --> move to mainActivity
        SessionManagement sessionManagement = new SessionManagement(Login.this);
        int userID = sessionManagement.getSession();

        if(userID != -1) {
            //user id logged in and move to mainActivity
            moveToMainActivity();
        }else{

        }
    }

    public void userLogin(View view){
        if(!vaildataPassword() || !vaildataUsername() ){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(10000);
        String login_username = username.getEditText().getText().toString();
        String login_userpassword = password.getEditText().getText().toString();

        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("email", login_username);
            jsonBodyObj.put("password", login_userpassword);
        }catch (JSONException e){
            e.printStackTrace();
        }

        callApi.json_post(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                if (response.getBoolean("status")) {

                    JSONObject data = response.getJSONObject("data");
                    currentUser = new ModelUser();
                    currentUser.setU_id(data.getInt("id"));
                    currentUser.setUsername(data.getString("username"));
                    currentUser.setEmail(data.getString("email"));
                    currentUser.setCreated_at(data.getString("created_at"));
                    currentUser.setUpdated_at(data.getString("updated_at"));
                    // suss(currentUser);

                    SessionManagement sessionManagement = new SessionManagement(Login.this);
                    sessionManagement.saveSession(currentUser);
                    moveToMainActivity();
                }else{
                    progressBar.setVisibility(View.GONE);
                    username.setError(getText(R.string.wrong_username));
                    username.requestFocus();
                    password.setError(getText(R.string.wrong_password));
                    password.requestFocus();
                }

            }
        },"/users/authenticate",jsonBodyObj);
    }

    private void suss(ModelUser currentUser) throws JSONException {
        //Log.d(TAG,currentUser.toString());
        SessionManagement sessionManagement = new SessionManagement(Login.this);
        sessionManagement.saveSession(currentUser);

        moveToMainActivity();
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(this, ReminderBroadcast.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonChlick = System.currentTimeMillis();
        long tsim = 1000*20;
        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonChlick + tsim, pendingIntent);
        Intent intent2 = new Intent(this, Dashboard.class);
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent2);
    }


    private Boolean vaildataPassword(){
        String val = password.getEditText().getText().toString();
        String passwordVal = "^" +
                //"(?=.*[0-9])" +   //at least 1 digit
                //"(?=.*[a-z])" +   //at least 1 lower case letter
                //"(?=.*[A-Z])" +   //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                //"(?=.*[@#$%^&+=])"+ //at least 1 special character
                "(?=\\S+$)" +       //no white spaces
                ".{4,}"+           // at least characters
                "$";
        if(val.isEmpty()){
            password.setError("Field cannot be empty");
            return false;
        }
//        else if(val.matches(passwordVal)){
//            password.setError("Password is to weak");
//            return false;
//        }
        else{
            password.setError(null);
            return true;
        }
    }

    private Boolean vaildataUsername(){
        String val = username.getEditText().getText().toString();
        String emailPatten = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(val.isEmpty()){
            username.setError("Field cannot be empty");
            return false;
        }else if(val.matches(emailPatten)){
            username.setError("Invalid Username");
            return false;
        }else{
            username.setError(null);
            return true;
        }
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "'";
            String description = "";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifyLemubit", name, importance);
            channel.setDescription(description);
        }
    }
}
