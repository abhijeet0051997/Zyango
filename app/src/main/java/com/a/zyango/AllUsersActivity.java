package com.a.zyango;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.a.zyango.POJO.Users;
import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.List;

public class AllUsersActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private RecyclerView firebase_recycler_view;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseRecyclerAdapter<Users, AllUsersActivity.UsersViewHolder> mAdapter;
    private ProgressBar progressBar;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_users);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24px);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progressbar);

        final List<Users> users = new ArrayList<>();
        mRef = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        firebase_recycler_view = findViewById(R.id.firebase_recycler);
        firebase_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        mRef.keepSynced(true);
        FirebaseRecyclerOptions options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(mRef, Users.class)
                .build();
        progressBar.setVisibility(View.VISIBLE);
        mAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.all_users_item_layout, viewGroup, false);
                return new UsersViewHolder(v);
            }

            @Override
            protected void onBindViewHolder(@NonNull UsersViewHolder viewHolder, final int position, @NonNull final Users model) {
                progressBar.setVisibility(View.GONE);
                viewHolder.name.setText(model.getDisplay_name());
                viewHolder.status.setText(model.getStatus());
                Glide.with(AllUsersActivity.this)
                        .load(model.getThumb_pic())
                        .placeholder(R.drawable.avataar)
                        .into(viewHolder.imageView);
                viewHolder.mItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String id = getRef(position).getKey();
                        if (id.equals(mAuth.getCurrentUser().getUid())) {
                            startActivity(new Intent(AllUsersActivity.this, SettingsActivity.class));
                        } else {
                            Intent intent = new Intent(AllUsersActivity.this, ProfileActivity.class);
                            intent.putExtra("Id", id);
                            startActivity(intent);
                        }
                    }
                });
            }
        };
        firebase_recycler_view.setAdapter(mAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        TextView name, status;
        View mItemView;
        CircularImageView imageView;

        public UsersViewHolder(@NonNull View itemView) {
            super(itemView);
            mItemView = itemView;
            name = itemView.findViewById(R.id.name);
            status = itemView.findViewById(R.id.status);
            imageView = itemView.findViewById(R.id.circular_image);
        }


    }
}


