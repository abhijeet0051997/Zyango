package com.a.zyango;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.a.zyango.Adapters.MessageAdapter;
import com.a.zyango.POJO.Messages;
import com.a.zyango.POJO.TimeAgoConversion;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView toolbar_name, toolbar_lastseen;
    private CircularImageView thumb_image_view;
    private String mUser_id;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private EditText writeMessage;
    private ImageView sendMessage;
    private RecyclerView message_recycler;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Messages> messages = new ArrayList<>();
    private MessageAdapter adapter;
    private Boolean onlinestatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24px);
        actionBar.setDisplayHomeAsUpEnabled(true);
        LayoutInflater Inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        toolbar_name = findViewById(R.id.name_toolbar);
        toolbar_lastseen = findViewById(R.id.lastSeen_toolbar);
        thumb_image_view = findViewById(R.id.circular_thumb_image);
        mUser_id = getIntent().getStringExtra("Id");
        writeMessage = findViewById(R.id.write_message);
        sendMessage = findViewById(R.id.send_message);
        message_recycler = findViewById(R.id.message_Recycler);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mRef.child("Chats").child(mUser_id).child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("timestamp")) {
                    String status = dataSnapshot.child("timestamp").getValue().toString();
                    onlinestatus = status.equals("online");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRef.child("Users").child(mUser_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("display_name").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_pic").getValue().toString();
                toolbar_name.setText(name);
                Glide.with(getApplicationContext())
                        .load(thumb_image)
                        .placeholder(R.drawable.avataar)
                        .into(thumb_image_view);
                if (online.equals("true")) {
                    toolbar_lastseen.setText("Online");

                } else {
                    String lastSeen = TimeAgoConversion.getTimeAgo(Long.parseLong(online));
                    toolbar_lastseen.setText(lastSeen);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Map chatAddMap = new HashMap();
        chatAddMap.put("timestamp", "online");

        Map Query = new HashMap();
        Query.put("Chats/" + mAuth.getCurrentUser().getUid() + "/" + mUser_id, chatAddMap);
        mRef.updateChildren(Query, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable final DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError == null) {
                    mRef.child("Messages").child(mAuth.getCurrentUser().getUid()).child(mUser_id)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChildren()) {
                                        for (DataSnapshot d : dataSnapshot.getChildren()) {
                                            Long timestamp = (Long) d.child("timestamp").getValue();
                                            Boolean seen = (boolean) d.child("seen").getValue();
                                            String from = d.child("from").getValue().toString();
                                            Map messageMap = new HashMap();
//
                                            if (!from.equals(mAuth.getCurrentUser().getUid()) && !seen) {
                                                messageMap.put("seen", true);
                                                mRef.child("Messages").child(mAuth.getCurrentUser().getUid())
                                                        .child(mUser_id).child(d.getKey()).updateChildren(messageMap);
                                                mRef.child("Messages").child(mUser_id).child(mAuth.getCurrentUser()
                                                        .getUid()).child(d.getKey()).updateChildren(messageMap);
                                                mRef.child("LastMessages").child(mAuth.getCurrentUser().getUid())
                                                        .child(mUser_id).updateChildren(messageMap);
                                                mRef.child("LastMessages").child(mUser_id).child(mAuth.getCurrentUser()
                                                        .getUid()).updateChildren(messageMap);

                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                }
            }
        });

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        mRef.child("Messages").child(mAuth.getCurrentUser().getUid()).child(mUser_id).keepSynced(true);
        adapter = new MessageAdapter(this, messages, mAuth.getCurrentUser().getUid());
        mRef.child("Messages").child(mAuth.getCurrentUser().getUid()).child(mUser_id)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String key = dataSnapshot.getKey();
                        String from = dataSnapshot.child("from").getValue().toString();
                        String message = dataSnapshot.child("message").getValue().toString();
                        String type = dataSnapshot.child("type").getValue().toString();
                        Boolean seen = (Boolean) dataSnapshot.child("seen").getValue();
                        Long timestamp = (Long) dataSnapshot.child("timestamp").getValue();
                        Messages message1 = new Messages(message, from, type, key, timestamp, seen);
                        messages.add(message1);
                        message_recycler.scrollToPosition(messages.size() - 1);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        mRef.child("Chats").child(mUser_id).child(mAuth.getCurrentUser().getUid())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot data) {
                                        if (data.hasChildren()) {
                                            String timestamp = data.child("timestamp").getValue().toString();
                                            if (timestamp.equals("online")) {
                                                for (int i = 0; i < messages.size(); i++) {
                                                    if (messages.get(i).getKey().equals(dataSnapshot.getKey())) {
                                                        messages.get(i).setSeen(true);
                                                        adapter.notifyDataSetChanged();

                                                    }
                                                }
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

        message_recycler.setLayoutManager(new LinearLayoutManager(this));
        message_recycler.setAdapter(adapter);

    }

    private void sendMessage() {
        final String message = writeMessage.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            if (onlinestatus) {
                Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("type", "text");
                messageMap.put("seen", true);
                messageMap.put("from", mAuth.getCurrentUser().getUid());
                messageMap.put("timestamp", ServerValue.TIMESTAMP);

                DatabaseReference pushRef = mRef.child("Messages")
                        .child(mAuth.getCurrentUser().getUid()).child(mUser_id).push();
                String push_id = pushRef.getKey();

                Map mssgMap = new HashMap();
                mssgMap.put("Messages/" + mAuth.getCurrentUser().getUid() + "/" + mUser_id + "/" + push_id, messageMap);
                mssgMap.put("Messages/" + mUser_id + "/" + mAuth.getCurrentUser().getUid() + "/" + push_id, messageMap);

                mRef.updateChildren(mssgMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.v("database error", databaseError.getMessage());
                        }
                    }
                });
                writeMessage.setText("");
                Map lastMessageMap = new HashMap();
                lastMessageMap.put("message", message);
                lastMessageMap.put("type", "text");
                lastMessageMap.put("seen", true);
                lastMessageMap.put("from", mAuth.getCurrentUser().getUid());
                lastMessageMap.put("timestamp", ServerValue.TIMESTAMP);
                Map lastmssgMap = new HashMap();
                lastmssgMap.put("LastMessages/" + mAuth.getCurrentUser().getUid() + "/" + mUser_id, lastMessageMap);
                lastmssgMap.put("LastMessages/" + mUser_id + "/" + mAuth.getCurrentUser().getUid(), lastMessageMap);
                mRef.updateChildren(lastmssgMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                        }
                    }
                });


            } else {
                Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("type", "text");
                messageMap.put("seen", false);
                messageMap.put("from", mAuth.getCurrentUser().getUid());
                messageMap.put("timestamp", ServerValue.TIMESTAMP);

                DatabaseReference pushRef = mRef.child("Messages")
                        .child(mAuth.getCurrentUser().getUid()).child(mUser_id).push();
                String push_id = pushRef.getKey();

                Map mssgMap = new HashMap();
                mssgMap.put("Messages/" + mAuth.getCurrentUser().getUid() + "/" + mUser_id + "/" + push_id, messageMap);
                mssgMap.put("Messages/" + mUser_id + "/" + mAuth.getCurrentUser().getUid() + "/" + push_id, messageMap);

                mRef.updateChildren(mssgMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                        }
                    }
                });
                writeMessage.setText("");
                Map lastMessageMap = new HashMap();
                lastMessageMap.put("message", message);
                lastMessageMap.put("type", "text");
                lastMessageMap.put("seen", false);
                lastMessageMap.put("from", mAuth.getCurrentUser().getUid());
                lastMessageMap.put("timestamp", ServerValue.TIMESTAMP);
                Map lastmssgMap = new HashMap();
                lastmssgMap.put("LastMessages/" + mAuth.getCurrentUser().getUid() + "/" + mUser_id, lastMessageMap);
                lastmssgMap.put("LastMessages/" + mUser_id + "/" + mAuth.getCurrentUser().getUid(), lastMessageMap);
                mRef.updateChildren(lastmssgMap, new DatabaseReference.CompletionListener() {
                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                        if (databaseError != null) {
                            Log.v("database error", databaseError.getMessage());
                        }
                    }
                });

            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Map chatAddMap = new HashMap();
        chatAddMap.put("timestamp", "online");

        Map Query = new HashMap();
        Query.put("Chats/" + mAuth.getCurrentUser().getUid() + "/" + mUser_id, chatAddMap);
        mRef.updateChildren(Query, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                }
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        Map chatAddMap = new HashMap();
        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);

        Map Query = new HashMap();
        Query.put("Chats/" + mAuth.getCurrentUser().getUid() + "/" + mUser_id, chatAddMap);
        mRef.updateChildren(Query, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                if (databaseError != null) {
                }
            }
        });
    }

}
