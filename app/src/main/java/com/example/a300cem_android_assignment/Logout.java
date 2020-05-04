package com.example.a300cem_android_assignment;

import android.content.Context;
import android.content.Intent;

import com.example.a300cem_android_assignment.Session.SessionManagement;

public class Logout {
    public static void logout(Context context){
        SessionManagement sessionManagement = new SessionManagement(context);
        sessionManagement.removeSession();
        moveToLogin(context);
    }
    private static void moveToLogin(Context context){
        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
