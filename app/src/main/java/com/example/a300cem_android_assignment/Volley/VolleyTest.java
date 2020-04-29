package com.example.a300cem_android_assignment.Volley;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.a300cem_android_assignment.CallApi;
import com.example.a300cem_android_assignment.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyTest extends AppCompatActivity {
    Button get_request_button;
    TextView get_response_text;
    CallApi callApi = new CallApi();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volley);
        get_request_button = findViewById(R.id.get_data);
        get_response_text = findViewById(R.id.get_response_data);

        get_request_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                callApi.json_get(new CallApi.VolleyCallback() {
//                    @Override
//                    public void onSuccessResponse(JSONObject result) {
//                        get_response_text.setText(result.toString());
//                    }
//                },"/customers");


                JSONObject jsonBodyObj = new JSONObject();
                try{
                    jsonBodyObj.put("email", "admin");
                    jsonBodyObj.put("password", "admin");
                }catch (JSONException e){
                    e.printStackTrace();
                }

                callApi.json_post(new CallApi.VolleyCallback() {
                    @Override
                    public void onSuccessResponse(JSONObject response) {
                        get_response_text.setText(response.toString());
                    }
                },"/users/authenticate",jsonBodyObj);
            }
        });
    }
}
