package com.example.a300cem_android_assignment.HomeAdapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.a300cem_android_assignment.CallApi;
import com.example.a300cem_android_assignment.GroupChatActivity;
import com.example.a300cem_android_assignment.R;
import com.example.a300cem_android_assignment.Volley.AppController;
import com.example.a300cem_android_assignment.models.ModelChatroom;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MostViewedAdpater extends RecyclerView.Adapter<MostViewedAdpater.MostViewedViewHolder> {

    ArrayList<MostViewedHelperClass> mostViewedLocations;
    ModelChatroom modelChatroom;
    Context mContext;
    public MostViewedAdpater(ArrayList<MostViewedHelperClass> mostViewedLocations, Context context) {
        this.mostViewedLocations = mostViewedLocations;
        this.mContext = context;
    }

    @NonNull
    @Override
    public MostViewedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.most_viewed_card_design, parent, false);
        MostViewedViewHolder mostViewedViewHolder = new MostViewedViewHolder(view);
        return mostViewedViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MostViewedViewHolder holder, int position) {
        final MostViewedHelperClass helperClass = mostViewedLocations.get(position);
        StringtoImage(holder,helperClass.getImage());
        //holder.image.setImageResource(helperClass.getImage());
        holder.title.setText(helperClass.getTitle());
        holder.desc.setText(helperClass.getDescription());
        String participant = mContext.getResources().getString(R.string.participant);
        holder.participant.setText(helperClass.getParticipants() + participant);
        final int id = helperClass.getId();

        holder.mostViewedRecyclerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setClickedChartoom(id);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mostViewedLocations.size();
    }

    public static class MostViewedViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title, desc, participant;
        RelativeLayout mostViewedRecyclerLayout;

        public MostViewedViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.ms_image);
            title = itemView.findViewById(R.id.ms_title);
            desc = itemView.findViewById(R.id.ms_desc);
            participant = itemView.findViewById(R.id.participant);
            mostViewedRecyclerLayout = itemView.findViewById(R.id.mostViewedRecyclerLayout);
        }
    }

    private void StringtoImage(final MostViewedViewHolder holder, String images){
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
                holder.image.setImageResource(R.mipmap.ic_corgi_foreground);
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.image.setImageBitmap(response.getBitmap());
            }
        });

    }

    private void setClickedChartoom(int id) {
        CallApi callApi = new CallApi();
        callApi.json_get(new CallApi.VolleyCallback() {
            @Override
            public void onSuccessResponse(JSONObject result) throws JSONException {
                JSONObject data = result.getJSONObject("data");
                modelChatroom = new ModelChatroom();
                modelChatroom.setChatroom_id(data.getInt("id"));
                modelChatroom.setCreated_by(data.getInt("created_by"));
                modelChatroom.setChartroom_name(data.getString("chatroom_name"));
                modelChatroom.setChatroom_desc(data.getString("chatroom_desc"));
                modelChatroom.setChatroom_icon(data.getString("chatroom_image"));
                modelChatroom.setCreated_at(data.getString("created_at"));
                modelChatroom.setLatitude(data.getDouble("latitude"));
                modelChatroom.setLongitude(data.getDouble("longitude"));
                nextAcitiy(modelChatroom);
            }
        }, "/chatrooms/querybyId/"+ id);
    }

    private void nextAcitiy(ModelChatroom modelChatroom) {
        Intent intent = new Intent(mContext, GroupChatActivity.class);
        intent.putExtra("currentChatroom", modelChatroom);
        mContext.startActivity(intent);
    }

}
