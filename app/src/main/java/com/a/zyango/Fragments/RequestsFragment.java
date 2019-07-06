package com.a.zyango.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a.zyango.POJO.Constants;
import com.a.zyango.POJO.FriendRequest;
import com.a.zyango.ProfileActivity;
import com.a.zyango.R;
import com.a.zyango.StartActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.a.zyango.POJO.Constants.REQUEST_TYPE;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {


    FirebaseAuth mAuth;
    DatabaseReference mRef;
    DatabaseReference mRootRef;
    List<FriendRequest> friendRequests = new ArrayList<>();
    FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder> adapter;
    RecyclerView request_recycler;
    boolean keyPresent = false;

    public RequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_requests, container, false);

        mAuth = FirebaseAuth.getInstance();

        mRootRef = FirebaseDatabase.getInstance().getReference();
        if (mAuth.getCurrentUser() != null) {
            mRef = FirebaseDatabase.getInstance().getReference().child("Friend_requests").child(mAuth.getCurrentUser().getUid());


            request_recycler = v.findViewById(R.id.request_recycler);
            FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<FriendRequest>()
                    .setQuery(mRef, FriendRequest.class)
                    .build();
            adapter = new FirebaseRecyclerAdapter<FriendRequest, FriendRequestViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final FriendRequestViewHolder friendRequestViewHolder, final int i,
                                                @NonNull FriendRequest friendRequest) {
                    if (friendRequest.getRequest_type().equals(Constants.RECIEVED)) {
                        String key = getRef(i).getKey();
                        DatabaseReference mref = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
                        mref.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChildren()) {
                                    String name = dataSnapshot.child("display_name").getValue().toString();
                                    String thumb = dataSnapshot.child("thumb_pic").getValue().toString();
                                    friendRequestViewHolder.name.setText(name);
                                    Glide.with(getActivity())
                                            .load(thumb)
                                            .placeholder(R.drawable.avataar)
                                            .into(friendRequestViewHolder.image);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    } else {
                        friendRequestViewHolder.itemView.setVisibility(View.GONE);
                        friendRequestViewHolder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));

                    }
                    friendRequestViewHolder.view_profile.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String user_id = getRef(i).getKey();
                            Intent intent = new Intent(getActivity(), ProfileActivity.class);
                            intent.putExtra("Id", user_id);
                            startActivity(intent);
                        }
                    });
                    friendRequestViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SimpleDateFormat Format = new SimpleDateFormat("dd-MM-yy", Locale.US);
                            String date = Format.format(new Date());
                            Map acceptFriendRequestMap = new HashMap();
                            String user_id = getRef(i).getKey();
                            acceptFriendRequestMap.put("Friends/" + mAuth.getCurrentUser().getUid() + "/" + user_id + "/" + "date", date);
                            acceptFriendRequestMap.put("Friends/" + user_id + "/" + mAuth.getCurrentUser().getUid() + "/" + "date", date);
                            acceptFriendRequestMap.put("Friend_requests/" + mAuth.getCurrentUser().getUid() + "/" + user_id + "/" + REQUEST_TYPE, null);
                            acceptFriendRequestMap.put("Friend_requests/" + user_id + "/" + mAuth.getCurrentUser().getUid() + "/" + REQUEST_TYPE, null);
                            mRootRef.updateChildren(acceptFriendRequestMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {

                                    } else {
                                        Toast.makeText(getActivity(), databaseError
                                                .getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    });

                    friendRequestViewHolder.decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String user_id = getRef(i).getKey();
                            Map cancelFriendRequestMap = new HashMap();
                            cancelFriendRequestMap.put("Friend_requests/" + mAuth.getCurrentUser().getUid() + "/" + user_id + "/" + REQUEST_TYPE, null);
                            cancelFriendRequestMap.put("Friend_requests/" + user_id + "/" + mAuth.getCurrentUser().getUid() + "/" + REQUEST_TYPE, null);
                            mRootRef.updateChildren(cancelFriendRequestMap, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                    if (databaseError == null) {

                                    } else {
                                        Toast.makeText(getActivity(), databaseError.getMessage()
                                                , Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                        }
                    });
                }

                @NonNull
                @Override
                public FriendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.friend_request_layout, parent, false);
                    return new FriendRequestViewHolder(v);
                }
            };
            request_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            request_recycler.setAdapter(adapter);

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

    class FriendRequestViewHolder extends RecyclerView.ViewHolder {
        CircularImageView image;
        TextView name;
        MaterialButton view_profile, accept, decline;

        public FriendRequestViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);
            view_profile = itemView.findViewById(R.id.view_profile);
            accept = itemView.findViewById(R.id.accept_request);
            decline = itemView.findViewById(R.id.decline_request);
        }
    }


}