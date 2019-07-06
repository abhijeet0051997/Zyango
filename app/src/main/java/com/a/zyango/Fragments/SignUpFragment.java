package com.a.zyango.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.a.zyango.MainActivity;
import com.a.zyango.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignUpFragment extends Fragment implements View.OnClickListener {


    private Toolbar toolbar;
    private TextInputLayout name, email, password;
    private MaterialButton signup;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private ProgressDialog dialog;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_sign_up, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Sign Up");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        name = v.findViewById(R.id.signup_name);
        email = v.findViewById(R.id.signup_email);
        password = v.findViewById(R.id.signup_password);
        signup = v.findViewById(R.id.signup_btn);
        signup.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();


        return v;


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_btn:
                createNewUsers();
                break;
        }
    }

    private void createNewUsers() {
        String nameStr = name.getEditText().getText().toString();
        final String emailStr = email.getEditText().getText().toString();
        String passwordStr = password.getEditText().getText().toString();
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Hold on");
        dialog.setMessage("Please wait while we create your account");
        dialog.setCancelable(false);
        dialog.show();
        mAuth.createUserWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    HashMap<String, String> map = new HashMap<>();
                    map.put("display_name", name.getEditText().getText().toString());
                    map.put("status", "Hello There ! I am using Zyango");
                    map.put("pro_pic", "default");
                    map.put("thumb_pic", "default");
                    mRef.child("Users").child(mAuth.getCurrentUser().getUid()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(getActivity(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                dialog.dismiss();
                                startActivity(intent);
                            }
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
            }
        });

    }
}
