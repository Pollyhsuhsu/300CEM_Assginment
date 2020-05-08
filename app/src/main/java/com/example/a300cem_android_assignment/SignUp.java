package com.example.a300cem_android_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.example.a300cem_android_assignment.models.ModelUser;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    TextInputLayout username,email,sign_password,sign_confirm_password;
    CallApi callApi = new CallApi();
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign_up);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        sign_password = findViewById(R.id.password);
        sign_confirm_password = findViewById(R.id.confirm_password);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
    }

    public void signup(View view){
        if(!vaildataPassword() || !vaildataUsername() || !vaildataEmail()){
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(10000);
        String signUp_email = email.getEditText().getText().toString();
        String signUp_username = username.getEditText().getText().toString();
        String signUp_userpassword = sign_confirm_password.getEditText().getText().toString();

        JSONObject jsonBodyObj = new JSONObject();
        try{
            jsonBodyObj.put("username", signUp_username);
            jsonBodyObj.put("email", signUp_email);
            jsonBodyObj.put("password", signUp_userpassword);
        }catch (JSONException e){
            e.printStackTrace();
        }

        callApi.json_post(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject response) throws JSONException {
                if (response.getBoolean("status")) {
                    ModelUser currentUser = new ModelUser();
                    currentUser.setU_id(response.getInt("insertid"));

                    SessionManagement sessionManagement = new SessionManagement(SignUp.this);
                    sessionManagement.saveSession(currentUser);
                    moveToMainActivity();
                }else{
                    progressBar.setVisibility(View.GONE);

                }
            }
        },"/users/register",jsonBodyObj);
    }

    private void moveToMainActivity() {
        Intent intent = new Intent(this, UserProfile.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private Boolean vaildataPassword(){
        String val = sign_password.getEditText().getText().toString();
        String com_val = sign_confirm_password.getEditText().getText().toString();
        System.out.println("password" +val+" " + com_val +" ");
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
            sign_password.setError("Field cannot be empty");
            sign_confirm_password.setError("Field cannot be empty");
            return false;
        }else if (!val.equals(com_val)){
            sign_confirm_password.setError("These passwords don't match. Try again?");
            return false;
        }
//        else if(val.matches(passwordVal)){
//            password.setError("Password is to weak");
//            return false;
//        }
        else{
            sign_password.setError(null);
            sign_confirm_password.setError(null);
            return true;
        }
    }

    private Boolean vaildataUsername(){
        String val = username.getEditText().getText().toString();
        if(val.isEmpty()){
            username.setError("Field cannot be empty");
            return false;
        }else{
            username.setError(null);
            return true;
        }
    }

    private Boolean vaildataEmail(){
        String val = email.getEditText().getText().toString();
        String emailPatten = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(val.isEmpty()){
            email.setError("Field cannot be empty");
            return false;
        }else if(!val.matches(emailPatten)){
            email.setError("Invalid Email");
            return false;
        }else{
            email.setError(null);
            return true;
        }
    }

}
