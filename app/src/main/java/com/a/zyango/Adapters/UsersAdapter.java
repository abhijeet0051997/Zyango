package com.a.zyango.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a.zyango.POJO.Users;
import com.a.zyango.R;
import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {
    Context context;
    List<Users> Data = new ArrayList<>();

    public UsersAdapter(Context context, List<Users> data) {
        this.context = context;
        Data = data;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.all_users_item_layout, parent, false);
        return new UsersViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.name.setText(Data.get(position).getDisplay_name());
        holder.status.setText(Data.get(position).getStatus());
        Glide.with(context)
                .load(Data.get(position).getPro_pic())
                .placeholder(R.drawable.avataar)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return Data.size();
    }

    class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        CircularImageView image;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            image = itemView.findViewById(R.id.circular_image);

        }
    }
}
