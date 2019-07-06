package com.a.zyango.Fragments;


import android.content.Intent;
import android.os.Bundle;
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
import com.a.zyango.POJO.Friends;
import com.a.zyango.ProfileActivity;
import com.a.zyango.R;
import com.a.zyango.StartActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
public class FriendsFragmnet extends Fragment {


    private RecyclerView friendsRecycler;
    private FirebaseRecyclerAdapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef, mUsersRef;

    public FriendsFragmnet() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_friends_fragmnet, container, false);
        friendsRecycler = v.findViewById(R.id.friends_recycler);
        friendsRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mRef = FirebaseDatabase.getInstance().getReference().child("Friends").child(mAuth.getCurrentUser().getUid());
            mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Friends>()
                    .setQuery(mRef, Friends.class)
                    .build();
            adapter = new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                @NonNull
                @Override
                public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friends_item_view_layout, parent, false);
                    return new FriendsViewHolder(v);
                }

                @Override
                protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, final int i, @NonNull Friends friends) {
                    friendsViewHolder.date.setText("Since " + friends.getDate());
                    String user_id = getRef(i).getKey();
                    mUsersRef.child(user_id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String display_name = dataSnapshot.child("display_name").getValue().toString();
                            String Status = dataSnapshot.child("status").getValue().toString();
                            String thumb_pic = dataSnapshot.child("thumb_pic").getValue().toString();
                            if (dataSnapshot.hasChild("online")) {
                                String status = dataSnapshot.child("online").getValue().toString();
                                if (status.equals("true")) {
                                    friendsViewHolder.online.setVisibility(View.VISIBLE);
                                } else {
                                    friendsViewHolder.online.setVisibility(View.INVISIBLE);
                                }
                            }
                            friendsViewHolder.name.setText(display_name);
                            friendsViewHolder.status.setText(Status);
                            Glide.with(getActivity())
                                    .load(thumb_pic)
                                    .placeholder(R.drawable.avataar)
                                    .into(friendsViewHolder.image);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    friendsViewHolder.friendsItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
                            View view = LayoutInflater.from(getActivity()).inflate(R.layout.bottomsheet_select_options, null);
                            TextView openProfile = view.findViewById(R.id.open_profile);
                            TextView sendMessage = view.findViewById(R.id.send_message);
                            final String id = getRef(i).getKey();
                            openProfile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), ProfileActivity.class);
                                    intent.putExtra("Id", id);
                                    startActivity(intent);
                                }
                            });
                            sendMessage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    intent.putExtra("Id", id);
                                    startActivity(intent);
                                }
                            });
                            dialog.setContentView(view);
                            dialog.show();
                        }
                    });
                }
            };
            friendsRecycler.setAdapter(adapter);
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

    class FriendsViewHolder extends RecyclerView.ViewHolder {

        TextView name, status, date;
        CircularImageView image;
        ImageView online;
        View friendsItemView;

        public FriendsViewHolder(@NonNull View itemView) {
            super(itemView);
            friendsItemView = itemView;
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            date = itemView.findViewById(R.id.date);
            online = itemView.findViewById(R.id.online_image);
            image = itemView.findViewById(R.id.circular_image);
        }

    }
}
