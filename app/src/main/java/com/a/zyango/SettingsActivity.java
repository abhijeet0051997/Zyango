package com.a.zyango;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.a.zyango.Adapters.ListViewAdapter;
import com.a.zyango.POJO.ListItem;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import id.zelory.compressor.Compressor;


public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listView;
    private List<ListItem> data;
    private BottomSheetDialog dialog;
    private String name, Status, url_pro, url_thumb;
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageReference mStorageRef;
    private FloatingActionButton camera_Floating_Action;
    private ImageView gradient_profile_image;
    private CircularImageView cir_profile_image;
    private ProgressBar progressBar;
    private DatabaseReference mUsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressBar = findViewById(R.id.progress_bar);
        gradient_profile_image = findViewById(R.id.gradient_profile_image);
        cir_profile_image = findViewById(R.id.circular_profile_image);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        dialog = new BottomSheetDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        mUsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        camera_Floating_Action = findViewById(R.id.camera_floating_action);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_24px);
        listView = findViewById(R.id.lisview);
        data = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);

        mRef.child("Users").child(mAuth.getCurrentUser().getUid()).keepSynced(true);
        mRef.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                url_pro = dataSnapshot.child("pro_pic").getValue().toString();
                url_thumb = dataSnapshot.child("thumb_pic").getValue().toString();

                Glide.with(getApplicationContext())
                        .load(url_pro)
                        .placeholder(R.drawable.avataar)
                        .into(gradient_profile_image);
                Glide.with(getApplicationContext())
                        .load(url_pro)
                        .placeholder(R.drawable.avataar)
                        .into(cir_profile_image);
                progressBar.setVisibility(View.GONE);
                name = dataSnapshot.child("display_name").getValue().toString();
                Status = dataSnapshot.child("status").getValue().toString();
                data = new ArrayList<>();
                data.add(new ListItem(R.drawable.ic_baseline_account_circle_24px, "Name", name, R.drawable.ic_baseline_border_color_24px));
                data.add(new ListItem(R.drawable.ic_baseline_info_24px, "Status", Status, R.drawable.ic_baseline_border_color_24px));
                ListViewAdapter adapter = new ListViewAdapter(SettingsActivity.this, data);
                adapter.notifyDataSetChanged();

                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        startBottomSheet(position);
                        break;
                    case 1:
                        startBottomSheet(position);
                        break;
                }
            }
        });


        camera_Floating_Action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);
            }
        });
    }

    private void startBottomSheet(int position) {
        if (position == 0) {
            View v = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null, false);
            final TextInputLayout nameInput = v.findViewById(R.id.bottomsheet_name);
            MaterialButton save = v.findViewById(R.id.bottom_sheet_save);
            MaterialButton cancel = v.findViewById(R.id.bottom_sheet_cancel);

            TextView title = v.findViewById(R.id.title_text);
            title.setText("Enter your name");
            nameInput.getEditText().setText(name);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String nameStr = nameInput.getEditText().getText().toString();
                    mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("display_name").setValue(nameStr);
                    dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setContentView(v);
            dialog.show();

        } else if (position == 1) {
            View v = getLayoutInflater().inflate(R.layout.bottom_sheet_layout, null, false);
            final TextInputLayout nameInput = v.findViewById(R.id.bottomsheet_name);
            MaterialButton save = v.findViewById(R.id.bottom_sheet_save);
            TextView title = v.findViewById(R.id.title_text);
            title.setText("Enter your status");
            nameInput.getEditText().setText(Status);
            MaterialButton cancel = v.findViewById(R.id.bottom_sheet_cancel);

            dialog.setContentView(v);
            dialog.show();
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String statusStr = nameInput.getEditText().getText().toString();
                    mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("status").setValue(statusStr);
                    dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final ProgressDialog dialog = new ProgressDialog(SettingsActivity.this);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                dialog.setTitle("Please wait");
                dialog.setCancelable(false);
                dialog.setMessage("Uploading your image .... ");
                dialog.show();
                Uri resultUri = result.getUri();

                try {
                    Bitmap actual = new Compressor(this)
                            .setMaxWidth(500)
                            .setMaxHeight(500)
                            .setQuality(40)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToBitmap(new File(resultUri.getPath()));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    actual.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                    byte[] actual_data = baos.toByteArray();
                    StorageReference sRef = mStorageRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("actual_profile_pic.jpg");
                    UploadTask uploadTask = sRef.putBytes(actual_data);
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mStorageRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("actual_profile_pic.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("pro_pic").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                    Bitmap thumb = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(20)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToBitmap(new File(resultUri.getPath()));

                    ByteArrayOutputStream baos_thumb = new ByteArrayOutputStream();
                    thumb.compress(Bitmap.CompressFormat.JPEG, 30, baos_thumb);
                    byte[] thumb_data = baos_thumb.toByteArray();
                    sRef = mStorageRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("thumb_profile_pic.jpg");
                    UploadTask uploadTask_thumb = sRef.putBytes(thumb_data);
                    uploadTask_thumb.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            mStorageRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("thumb_profile_pic.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    mRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("thumb_pic").setValue(uri.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                dialog.dismiss();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }


            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
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
}
