package com.example.mywearables2;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.wearable.activity.WearableActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends WearableActivity {

    private Button button;
    private static final long CONNECTION_TIME_OUT_MS = 100;
    private String message;
    private GoogleApiClient client;
    private String nodeId;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextView = (TextView) findViewById(R.id.text);

        button = (Button) findViewById(R.id.send);
        initApi();
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                message = "seconds remaining: " + millisUntilFinished / 1000;
                mTextView.setText(message);
            }

            public void onFinish() {
                mTextView.setText("done!");
            }
        }.start();

        // Enables Always-on
        setAmbientEnabled();
    }
    private void updateDisplay() {
        if (isAmbient()) {
            //existing code
            button.setTextColor(getResources().getColor(android.R.color.white));
        } else {
            //existing code
            button.setTextColor(getResources().getColor(android.R.color.black));
        }
    }

    private void initApi() {
        client = getGoogleApiClient(this);
        retrieveDeviceNode();
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                    Log.d("DEBUG_KEY", "size "+Integer.toString(nodes.size()));

                    Log.d("DEBUG_KEY", nodeId);
                }
                client.disconnect();
            }
        }).start();
    }

    public void onSendClick(View v) {
        if (nodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("DEBUG_KEY", "on click");
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, nodeId, message, null);
                    client.disconnect();
                    Log.d("DEBUG_KEY", "on click sent");

                }
            }).start();
        }
    }
}
