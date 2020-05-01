package com.example.a300cem_android_assignment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.a300cem_android_assignment.Dashboard;
import com.example.a300cem_android_assignment.GroupChatroomList;
import com.example.a300cem_android_assignment.R;
import com.example.a300cem_android_assignment.models.ModelChatroom;

import java.util.ArrayList;

public class GroupListAdapter extends ArrayAdapter<ModelChatroom> {

    private Context mcontext;
    int mResource;

    public GroupListAdapter(Context context, int resource, ArrayList<ModelChatroom> chatrooms) {
        super(context, resource, chatrooms);
        this.mcontext = context;
        this.mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        int group_id = getItem(position).getChatroom_id();
        int created_by = getItem(position).getCreated_by();
        final String group_name = getItem(position).getChartroom_name();
        String group_desc = getItem(position).getChatroom_desc();
        String group_icon = getItem(position).getChatroom_icon();
        String created_at = getItem(position).getCreated_at();
        ModelChatroom modelChatroom = new ModelChatroom(group_id,created_by,group_name,group_icon,group_desc,created_at);
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mResource, parent, false);

        RelativeLayout linear = (RelativeLayout)convertView.findViewById(R.id.linear);
                //ImageView groupIconIv;
        TextView groupTitleTv = (TextView) convertView.findViewById(R.id.groupTitleTv);
//        TextView nameTv
//        TextView messageTv
//        TextView timeTv;

        groupTitleTv.setText(group_name);
//        linear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApp, Dashboard.class);
//                intent.putExtra("currentUser", currentUser);
//                startActivity(intent);
//            }
//        });

        return convertView;
    }
}
