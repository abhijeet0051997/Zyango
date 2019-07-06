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

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {


    private Toolbar toolbar;
    private MaterialButton login;
    private TextInputLayout email, password;
    private FirebaseAuth mAuth;
    private AppCompatActivity activity;
    private ProgressDialog dialog;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        toolbar = v.findViewById(R.id.toolbar);
        email = v.findViewById(R.id.login_email);
        password = v.findViewById(R.id.login_password);
        login = v.findViewById(R.id.login_button);
        activity = (AppCompatActivity) getActivity();
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("Login");
        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        login.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();


        return v;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                loginUsers();
                break;

        }
    }

    private void loginUsers() {


        String emailStr = email.getEditText().getText().toString();
        String passwordStr = password.getEditText().getText().toString();
        dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Hold on");
        dialog.setMessage("Please wait while we login to your account");
        dialog.setCancelable(false);
        dialog.show();
        mAuth.signInWithEmailAndPassword(emailStr, passwordStr)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            dialog.dismiss();
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    }
                });
    }
}
