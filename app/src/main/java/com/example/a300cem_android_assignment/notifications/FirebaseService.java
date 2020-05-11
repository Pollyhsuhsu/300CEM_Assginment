package com.example.a300cem_android_assignment.notifications;

import android.content.Intent;

import com.example.a300cem_android_assignment.NyGroupChatroomList;
import com.example.a300cem_android_assignment.Session.SessionManagement;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
public class FirebaseService extends FirebaseInstanceIdService{
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            // do your stuff
        } else {
            mAuth.signInAnonymously();
        }
        updateToken(tokenRefresh);
    }

    private void updateToken(String tokenRefresh) {
        SessionManagement sessionManagement = new SessionManagement(FirebaseService.this);
        int currentUserID = sessionManagement.getSession();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(tokenRefresh);
        ref.child(String.valueOf(currentUserID)).setValue(token);
    }
}
