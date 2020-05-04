package com.example.a300cem_android_assignment.Session;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.a300cem_android_assignment.models.ModelUser;

public class SessionManagement {
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String SHARED_PREF_NAME = "session";
    String SESSION_KEY = "session_user";

    public SessionManagement(Context context){
        sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveSession(ModelUser user){
        //save session of user whenever user is logged in
        int id = user.getU_id();
        editor.putInt(SESSION_KEY, id).commit();
    }

    public int getSession(){
        //return user whose session is saved
       return sharedPreferences.getInt(SESSION_KEY,-1);
    }

    public void removeSession(){
        editor.putInt(SESSION_KEY, -1).commit();
    }
}
