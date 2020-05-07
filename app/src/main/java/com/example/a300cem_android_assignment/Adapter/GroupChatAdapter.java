package com.example.a300cem_android_assignment.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.a300cem_android_assignment.R;
import com.example.a300cem_android_assignment.models.ModelGroupChat;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class GroupChatAdapter extends RecyclerView.Adapter<GroupChatAdapter.HolderGroupChat>{

    private static final int MSG_TYPE_LEFT = 0;
    private static final int MSG_TYPE_Right = 1;

    private Context context;
    private ArrayList<ModelGroupChat> modelGroupChatsListList;
    private int currentUser_id;
    private int currentRoom_id;

    public GroupChatAdapter(Context context, ArrayList<ModelGroupChat> modelGroupChatsListList, int currentUserID, int currentRoomID) {
        this.context = context;
        this.modelGroupChatsListList = modelGroupChatsListList;
        currentUser_id = currentUserID;
    }

    @NonNull
    @Override
    public HolderGroupChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == MSG_TYPE_Right){
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_right, parent,false);
            return new HolderGroupChat(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.row_groupchat_left, parent,false);
            return new HolderGroupChat(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChat holder, int position) {
        ModelGroupChat model = modelGroupChatsListList.get(position);
        int senderUid = model.getSender_id();
        String timestamp = model.getTimestamp();
        String senderName = model.getSender_name();
        String message = model.getMessage();// if text message then contain message,if image message then contain url of the image stored in firebase stooge
        String messageType = model.getType();

        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(Long.parseLong(timestamp));
        String dataTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

        //set data
        if(messageType.equals("text")){
            //text message, hide imageView, Show messageTv
            holder.messageIv.setVisibility(View.GONE);
            holder.messageTv.setVisibility(View.VISIBLE);
            holder.messageTv.setText(message);
        }else{
            //image message, hide messageTv, show messageIv
            holder.messageIv.setVisibility(View.VISIBLE);
            holder.messageTv.setVisibility(View.GONE);
            try{
                Picasso.get().load(message).placeholder(R.drawable.ic_action_add_icon).into(holder.messageIv);
            }catch (Exception e){
                holder.messageIv.setImageResource(R.drawable.photo1);
            }
        }



        if(senderUid != currentUser_id){
            holder.nameTv.setText(senderName);
        }
        holder.timeTv.setText(dataTime);


        //setUserName(model, holder);
    }

    private void setUserName(ModelGroupChat model, HolderGroupChat holder) {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
//        ref.orderByChild(Integer.toString(currentRoom_id))
    }

    @Override
    public int getItemCount() {
        return modelGroupChatsListList.size();
    }
    @Override
    public int getItemViewType(int position) {
        if(modelGroupChatsListList.get(position).getSender_id() == currentUser_id){
            return MSG_TYPE_Right;
        }else{
            return MSG_TYPE_LEFT;
        }
    }


    class HolderGroupChat extends RecyclerView.ViewHolder{
        private TextView nameTv,messageTv,timeTv;
        private View avatar;
        private ImageView messageIv;

        public HolderGroupChat(@NonNull View itemView) {
            super(itemView);

            nameTv = (TextView) itemView.findViewById(R.id.nameTv);
            messageTv =(TextView) itemView.findViewById(R.id.messageTv);
            timeTv = (TextView) itemView.findViewById(R.id.timeTv);
            avatar = (View) itemView.findViewById(R.id.avatar);
            messageIv =(ImageView) itemView.findViewById(R.id.messageIv);
        }

    }
}
