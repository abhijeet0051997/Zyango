package com.a.zyango;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static com.a.zyango.POJO.Constants.FRIENDS;
import static com.a.zyango.POJO.Constants.NOT_FRIENDS;
import static com.a.zyango.POJO.Constants.RECIEVED;
import static com.a.zyango.POJO.Constants.REQUEST_RECIEVED;
import static com.a.zyango.POJO.Constants.REQUEST_SENT;
import static com.a.zyango.POJO.Constants.REQUEST_TYPE;
import static com.a.zyango.POJO.Constants.SENT;

public class ProfileActivity extends AppCompatActivity {
    private TextInputLayout name, status;
    private MaterialButton send_friend_request, decline_friend_request;
    private ImageView profile_image, back_button;
    private String user_id;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private DatabaseReference mFriendsRef;
    private DatabaseReference mFriendRequestRef;
    private int mCurrentState;
    private String date;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        mAuth = FirebaseAuth.getInstance();
        mCurrentState = NOT_FRIENDS;
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        mFriendsRef = FirebaseDatabase.getInstance().getReference();
        mFriendRequestRef = FirebaseDatabase.getInstance().getReference();
        mRef = FirebaseDatabase.getInstance().getReference();
        back_button = findViewById(R.id.back_button);
        profile_image = findViewById(R.id.profile_image);
        name = findViewById(R.id.profile_name);
        status = findViewById(R.id.profile_status);
        send_friend_request = findViewById(R.id.send_Friend_request);
        decline_friend_request = findViewById(R.id.decline_friend_request);
        user_id = getIntent().getStringExtra("Id");
        decline_friend_request.setVisibility(View.GONE);
        send_friend_request.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.colorPrimaryDark));

        mRef.child("Users").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String pro_url = dataSnapshot.child("pro_pic").getValue().toString();
                String display_name = dataSnapshot.child("display_name").getValue().toString();
                String Status = dataSnapshot.child("status").getValue().toString();
                status.getEditText().setText(Status);
                name.getEditText().setText(display_name);
                Glide.with(getApplicationContext())
                        .load(pro_url)
                        .placeholder(R.drawable.avataar)
                        .into(profile_image);
                mFriendRequestRef.child("Friend_requests").child(mAuth.getCurrentUser().getUid())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild(user_id)) {
                                    String request_type = dataSnapshot.child(user_id).child(REQUEST_TYPE).getValue().toString();
                                    if (request_type.equals(RECIEVED)) {
                                        mCurrentState = REQUEST_RECIEVED;
                                        send_friend_request.setText("accept friend Request");
                                        send_friend_request.setBackgroundTintList(ContextCompat
                                                .getColorStateList(ProfileActivity.this, R.color.colorGreen));
                                        decline_friend_request.setVisibility(View.VISIBLE);
                                    } else {
                                        mCurrentState = REQUEST_SENT;
                                        send_friend_request.setText("cancel friend Request");
                                        send_friend_request.setBackgroundTintList(ContextCompat
                                                .getColorStateList(ProfileActivity.this, R.color.colorRed));
                                        decline_friend_request.setVisibility(View.GONE);
                                    }

                                } else {

                                    mFriendsRef.child("Friends").child(mAuth.getCurrentUser().getUid())
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(user_id)) {
                                                        mCurrentState = FRIENDS;
                                                        send_friend_request.setText("Unfriend");
                                                        send_friend_request.setBackgroundTintList(ContextCompat
                                                                .getColorStateList(ProfileActivity.this, R.color.colorRed));

                                                        decline_friend_request.setVisibility(View.GONE);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        send_friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                send_friend_request.setEnabled(false);
                //_____________________- send friend request -___________________

                if (mCurrentState == NOT_FRIENDS) {
                    Map sendFriendRequestMap = new HashMap();
                    sendFriendRequestMap.put("Friend_requests/" + mAuth.getCurrentUser().getUid() + "/" + user_id + "/" + REQUEST_TYPE, SENT);
                    sendFriendRequestMap.put("Friend_requests/" + user_id + "/" + mAuth.getCurrentUser().getUid() + "/" + REQUEST_TYPE, RECIEVED);
                    mFriendRequestRef.updateChildren(sendFriendRequestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mCurrentState = REQUEST_SENT;
                                send_friend_request.setEnabled(true);
                                send_friend_request.setText("cancel friend Request");
                                send_friend_request.setBackgroundTintList(ContextCompat
                                        .getColorStateList(ProfileActivity.this, R.color.colorRed));
                                decline_friend_request.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //_________________- Cancel Friend Request -____________________
                else if (mCurrentState == REQUEST_SENT) {
                    Map cancelFriendRequestMap = new HashMap();
                    cancelFriendRequestMap.put("Friend_requests/" + mAuth.getCurrentUser().getUid() + "/" + user_id + "/" + REQUEST_TYPE, null);
                    cancelFriendRequestMap.put("Friend_requests/" + user_id + "/" + mAuth.getCurrentUser().getUid() + "/" + REQUEST_TYPE, null);
                    mFriendRequestRef.updateChildren(cancelFriendRequestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mCurrentState = NOT_FRIENDS;
                                send_friend_request.setEnabled(true);
                                send_friend_request.setText("send friend Request");
                                send_friend_request.setBackgroundTintList(ContextCompat
                                        .getColorStateList(ProfileActivity.this, R.color.colorPrimaryDark));
                                decline_friend_request.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), databaseError.getMessage()
                                        , Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }
                //_________________- Accept Friend Request -____________________
                else if (mCurrentState == REQUEST_RECIEVED) {
                    SimpleDateFormat Format = new SimpleDateFormat("dd-MM-yy", Locale.US);
                    date = Format.format(new Date());
                    Map acceptFriendRequestMap = new HashMap();
                    acceptFriendRequestMap.put("Friends/" + mAuth.getCurrentUser().getUid() + "/" + user_id + "/" + "Date", date);
                    acceptFriendRequestMap.put("Friends/" + user_id + "/" + mAuth.getCurrentUser().getUid() + "/" + "Date", date);
                    acceptFriendRequestMap.put("Friend_requests/" + mAuth.getCurrentUser().getUid() + "/" + user_id + "/" + REQUEST_TYPE, null);
                    acceptFriendRequestMap.put("Friend_requests/" + user_id + "/" + mAuth.getCurrentUser().getUid() + "/" + REQUEST_TYPE, null);
                    mFriendsRef.updateChildren(acceptFriendRequestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mCurrentState = FRIENDS;
                                send_friend_request.setEnabled(true);
                                send_friend_request.setText("Unfriend");
                                decline_friend_request.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getApplicationContext(), databaseError
                                        .getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }//_________________- Unfriend -____________________
                else if (mCurrentState == FRIENDS) {
                    Map unFriendMap = new HashMap();
                    unFriendMap.put("Friends/" + mAuth.getCurrentUser().getUid() + "/" + user_id, null);
                    unFriendMap.put("Friends/" + user_id + "/" + mAuth.getCurrentUser().getUid(), null);
                    mFriendsRef.updateChildren(unFriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mCurrentState = NOT_FRIENDS;
                                send_friend_request.setEnabled(true);
                                send_friend_request.setText("send friend Request");
                                send_friend_request.setBackgroundTintList(ContextCompat
                                        .getColorStateList(ProfileActivity.this, R.color.colorPrimaryDark));
                                decline_friend_request.setVisibility(View.GONE);

                            } else {
                                Toast.makeText(getApplicationContext(), databaseError
                                        .getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        decline_friend_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map declineFriendRequestMap = new HashMap();
                declineFriendRequestMap.put("Friend_requests/" + mAuth.getCurrentUser().getUid() + "/" + user_id + "/" + REQUEST_TYPE, null);
                declineFriendRequestMap.put("Friend_requests/" + user_id + "/" + mAuth.getCurrentUser().getUid() + "/" + REQUEST_TYPE, null);
                mFriendRequestRef.updateChildren(declineFriendRequestMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError == null) {
                            mCurrentState = NOT_FRIENDS;
                            send_friend_request.setEnabled(true);
                            send_friend_request.setText("send friend Request");
                            send_friend_request.setBackgroundTintList(ContextCompat
                                    .getColorStateList(ProfileActivity.this, R.color.colorPrimaryDark));
                            decline_friend_request.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(getApplicationContext(), databaseError
                                    .getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }

                });
            }
        });
    }


}
