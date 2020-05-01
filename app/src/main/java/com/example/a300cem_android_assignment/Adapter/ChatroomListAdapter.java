package com.example.a300cem_android_assignment.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a300cem_android_assignment.R;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChatroomListAdapter extends RecyclerView.Adapter<ChatroomListAdapter.HolderGroupChatList>{
    private Context context;
    private ArrayList<ModelChatroom> groupChatLists;

    public ChatroomListAdapter(Context context, ArrayList<ModelChatroom> groupChatLists) {
        this.context = context;
        this.groupChatLists = groupChatLists;
    }

    @NonNull
    @Override
    public HolderGroupChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat,parent, false);
        return new HolderGroupChatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChatList holder, int position) {
        // get data
        for(int i = 0; i < groupChatLists.size(); i++) {
            System.out.println(groupChatLists.get(i).getChatroom_id());
        }
        ModelChatroom modelChatroom = groupChatLists.get(position);
        int chatroom_id = modelChatroom.getChatroom_id();
        String chatroom_icon = modelChatroom.getChatroom_icon();
        String chatroom_title = modelChatroom.getChartroom_name();

        // set data
        holder.groupTitleTv.setText(chatroom_title);
//        try{
//            Picasso.get().load(chatroom_icon).placeholder(R.drawable.ic_group_primary).into(holder.groupIconIv);
//        }catch (Exception e){
//            holder.groupIconIv.setImageResource(R.drawable.ic_group_primary);
//        }

        //handle group click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class HolderGroupChatList extends RecyclerView.ViewHolder{

        private ImageView groupIconIv;
        private TextView groupTitleTv, nameTv,messageTv, timeTv;

        public HolderGroupChatList(@NonNull View itemView) {
            super(itemView);
            groupIconIv = itemView.findViewById(R.id.groupIconIv);
            groupTitleTv = itemView.findViewById(R.id.groupTitleTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
        }
    }
}
