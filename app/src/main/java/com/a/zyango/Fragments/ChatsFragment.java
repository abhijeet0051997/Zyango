package com.a.zyango.Fragments;


import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a.zyango.ChatActivity;
import com.a.zyango.POJO.Chat;
import com.a.zyango.POJO.TimeAgoConversion;
import com.a.zyango.R;
import com.a.zyango.StartActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {


    private RecyclerView chatRecycler;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private FirebaseRecyclerAdapter<Chat, ChatViewHolder> adapter;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_chats, container, false);
        chatRecycler = v.findViewById(R.id.chat_recycler);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mRef = FirebaseDatabase.getInstance().getReference().child("LastMessages").child(mAuth.getCurrentUser().getUid());
            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Chat>()
                    .setQuery(mRef, Chat.class)
                    .build();
            adapter = new FirebaseRecyclerAdapter<Chat, ChatViewHolder>(options) {
                @NonNull
                @Override
                public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item_layout, parent, false);
                    return new ChatViewHolder(v);
                }

                @Override
                protected void onBindViewHolder(@NonNull ChatViewHolder holder, int i, @NonNull Chat chat) {
                    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
                    mRootRef.child("Users").child(getRef(i).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String name = dataSnapshot.child("display_name").getValue().toString();
                            String thumb = dataSnapshot.child("thumb_pic").getValue().toString();
                            holder.name.setText(name);
                            Glide.with(getActivity())
                                    .load(thumb)
                                    .placeholder(R.drawable.avataar)
                                    .into(holder.imageView);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    String time = TimeAgoConversion.getTimeAgo(chat.getTimestamp());
                    holder.date.setText(time);
                    Log.v("mssg from", chat.getFrom());
                    if (chat.getFrom().equals(mAuth.getCurrentUser().getUid())) {
                        if (chat.getSeen()) {
                            holder.last_seen.setVisibility(View.VISIBLE);
                            holder.last_seen.setImageResource(R.drawable.ic_baseline_check_circle_24px);
                        } else {
                            holder.last_seen.setImageResource(R.drawable.ic_baseline_check_circle_outline_24px);
                        }
                        holder.last_message.setText(chat.getMessage());
                    } else {
                        holder.last_seen.setVisibility(View.GONE);
                        if (chat.getSeen()) {
                            holder.last_message.setTypeface(holder.last_message.getTypeface(), Typeface.NORMAL);
                            holder.last_message.setText(chat.getMessage());
                        } else {
                            Log.v("mssg", "here in bind");
                            holder.last_message.setTypeface(holder.last_message.getTypeface(), Typeface.BOLD);
                            holder.last_message.setText(chat.getMessage());
                        }
                    }
                    holder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String id = getRef(i).getKey();
                            Intent intent = new Intent(getActivity(), ChatActivity.class);
                            intent.putExtra("Id", id);
                            startActivity(intent);
                        }
                    });
                }


            };

            chatRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            chatRecycler.setAdapter(adapter);


        } else {
            Intent intent = new Intent(getActivity(), StartActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

        }
        return v;

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null) {
            adapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuth.getCurrentUser() != null) {
            adapter.stopListening();
        }

    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        CircularImageView imageView;
        ImageView last_seen;
        TextView name, last_message, date;
        View mView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            last_message = itemView.findViewById(R.id.last_message);
            date = itemView.findViewById(R.id.date);
            imageView = itemView.findViewById(R.id.circular_image);
            last_seen = itemView.findViewById(R.id.seen_image);
            mView = itemView;

        }
    }
}
