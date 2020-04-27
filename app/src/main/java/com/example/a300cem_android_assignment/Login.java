package com.example.a300cem_android_assignment;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class Login<punlic> extends AppCompatActivity {

    public static final String TAG = "MYTAG";
    String URLHTTP;
    Button callSignUp, login_btn;
    ImageView image;
    TextView logoText, sloganText;
    TextInputLayout username,password;
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

//        login_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                JSONObject jsonBodyObj = new JSONObject();
//                try{
//                    jsonBodyObj.put("email", "admin");
//                    jsonBodyObj.put("password", "admin");
//                }catch (JSONException e){
//                    e.printStackTrace();
//                }
//
//                callApi.json_post(new CallApi.VolleyCallback() {
//                    @Override
//                    public void onSuccessResponse(JSONObject response) {
//                        Log.d(TAG,response.toString());
//                    }
//                },"/users/authenticate",jsonBodyObj);
//            }
//        });
    }

    public void userLogin(View view){
        if(!vaildataPassword() || !vaildataEmail() ){
            return;
        }
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
            public void onSuccessResponse(JSONObject response) {
                Log.d(TAG,response.toString());
            }
        },"/users/authenticate",jsonBodyObj);


        Intent intent = new Intent(this, Chat.class);
        startActivity(intent);
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

    private Boolean vaildataEmail(){
        String val = username.getEditText().getText().toString();
        String emailPatten = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(val.isEmpty()){
            username.setError("Field cannot be empty");
            return false;
        }else if(val.matches(emailPatten)){
            username.setError("Invalid email address");
            return false;
        }else{
            username.setError(null);
            return true;
        }
    }
}
