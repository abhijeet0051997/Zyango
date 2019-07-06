package com.a.zyango.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.a.zyango.POJO.Messages;
import com.a.zyango.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    Context context;
    List<Messages> messages = new ArrayList<>();
    String currentUser;

    public MessageAdapter(Context context, List<Messages> messages, String currentUser) {
        this.context = context;
        this.messages = messages;
        this.currentUser = currentUser;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.message_layout, parent, false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        if (messages.get(position).getFrom().equals(currentUser)) {
            holder.bubbleLayout2.setVisibility(View.GONE);
            holder.bubbleLayout.setVisibility(View.VISIBLE);
            holder.message.setText(messages.get(position).getMessage());
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String time = formatter.format(messages.get(position).getTimestamp());
            holder.time.setText(time);
            if (messages.get(position).getSeen()) {
                holder.imageView.setImageResource(R.drawable.ic_baseline_check_circle_24px);
            } else {
                holder.imageView.setImageResource(R.drawable.ic_baseline_check_circle_outline_24px);

            }
        } else {
            holder.bubbleLayout2.setVisibility(View.VISIBLE);
            holder.bubbleLayout.setVisibility(View.GONE);
            holder.message2.setText(messages.get(position).getMessage());
            SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
            String time = formatter.format(messages.get(position).getTimestamp());
            holder.time2.setText(time);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView message, time, message2, time2;
        LinearLayout bubbleLayout, bubbleLayout2;
        ImageView imageView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            bubbleLayout = itemView.findViewById(R.id.bubble);
            time = itemView.findViewById(R.id.time);
            message2 = itemView.findViewById(R.id.message2);
            bubbleLayout2 = itemView.findViewById(R.id.bubble2);
            time2 = itemView.findViewById(R.id.time2);
            imageView = itemView.findViewById(R.id.seen_image);

        }
    }
}
