package com.example.a300cem_android_assignment.Adapter;

import android.content.Context;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.a300cem_android_assignment.R;
import com.example.a300cem_android_assignment.Volley.AppController;
import com.example.a300cem_android_assignment.models.ModelChatroom;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

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
        final String group_desc = getItem(position).getChatroom_desc();
        final String group_icon = getItem(position).getChatroom_icon();
        String created_at = getItem(position).getCreated_at();
        double longitude = getItem(position).getLongitude();
        double latitude = getItem(position).getLatitude();
        final double distance = getItem(position).getDistance();

        final ModelChatroom modelChatroom = new ModelChatroom(group_id, created_by, group_name, group_icon, group_desc, created_at, longitude, latitude, distance);
        LayoutInflater inflater = LayoutInflater.from(mcontext);
        convertView = inflater.inflate(mResource, parent, false);

        final ShimmerFrameLayout shimmerFrameLayout = (ShimmerFrameLayout) convertView.findViewById(R.id.shimmerFrameLayout);
        final TextView nameTv = (TextView) convertView.findViewById(R.id.nameTv);
        final TextView messageTv= (TextView) convertView.findViewById(R.id.messageTv);
        final TextView timeTv = (TextView) convertView.findViewById(R.id.timeTv);
        final ImageView groupIconIv = (ImageView) convertView.findViewById(R.id.groupIconIv);
        final TextView groupTitleTv = (TextView) convertView.findViewById(R.id.groupTitleTv);

        final String toStringcontext = mcontext+ "";

            shimmerFrameLayout.startShimmer();
            if (group_icon != null) {
                StringtoImage(group_icon, groupIconIv);
            }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setShimmer(null);

                if (toStringcontext.contains("NyGroupChatroomList")) {
                    timeTv.setText(formatDouble3(distance) + " km");
                    nameTv.setText(group_desc);
                    messageTv.setVisibility(View.GONE);
                } else {
                    messageTv.setText("");
                    timeTv.setText("");
                    nameTv.setText("");
                    loadLastMessage(modelChatroom, messageTv, timeTv, nameTv);
                }
                groupTitleTv.setText(group_name);
            }
        },3000);

        //loadLastMessage(modelChatroom, convertView,messageTv,timeTv,nameTv);
        return convertView;
    }

    public static String formatDouble3(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留2位小數
        nf.setMaximumFractionDigits(2);
        return nf.format(d);
    }

    private void StringtoImage(String images, final ImageView groupIconIv){
        Log.d("image", images);

        /*
        Picasso.get().load(Uri.parse(images))
        .error(R.drawable.ic_group_primary)
        .priority(Picasso.Priority.HIGH)
        .noFade()
        .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
        .into(groupIconIv);
        */

        ImageLoader imageLoader = AppController.getInstance().getImageLoader();
        imageLoader.get(images, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                groupIconIv.setImageResource(R.drawable.ic_group_primary);
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                groupIconIv.setImageBitmap(response.getBitmap());

            }
        });

    }


    private void loadLastMessage(ModelChatroom model, final TextView messageTv, final TextView timeTv, final TextView nameTv){
        //get last message from group

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(Integer.toString(model.getChatroom_id())).child("Messages").limitToLast(1) //get last item(message) form that child
                .addValueEventListener(new ValueEventListener() {
                    @Override

                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {

                            String message = "" + ds.child("message").getValue();
                            String timestamp = "" + ds.child("timestamp").getValue();
                            String sender_name = "" + ds.child("sender_name").getValue();
                            String messageType = "" + ds.child("type").getValue();

                            //convent time
                            //convent time stamp to dd/mm/yyy hh:mm am/pm
                            Calendar cal = Calendar.getInstance(Locale.ENGLISH);
                            cal.setTimeInMillis(Long.parseLong(timestamp));
                            String dataTime = DateFormat.format("dd/MM/yyyy hh:mm aa", cal).toString();

                            if(messageType.equals("image")){
                                messageTv.setText("Sent Photo");
                            }else{
                                messageTv.setText(message);
                            }

                            timeTv.setText(dataTime);
                            nameTv.setText(sender_name);

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        System.out.println(databaseError.toString());
                    }
                });
    }

    public enum Priority {
        LOW,
        NORMAL,
        HIGH
    }

}
