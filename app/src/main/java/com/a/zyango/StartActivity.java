package com.a.zyango;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.a.zyango.Fragments.LoginFragment;
import com.a.zyango.Fragments.SignUpFragment;
import com.google.android.material.button.MaterialButton;

public class StartActivity extends AppCompatActivity implements View.OnClickListener {
    private MaterialButton Login_btn;
    private MaterialButton SignUp_btn;
    private LinearLayout startPage;
    private FrameLayout container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        Login_btn = findViewById(R.id.alreadyAccount);
        SignUp_btn = findViewById(R.id.newAccont);
        startPage = findViewById(R.id.startPageLayout);
        container = findViewById(R.id.container);

        Login_btn.setOnClickListener(this);
        SignUp_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alreadyAccount:
                startFragment(new LoginFragment());
                break;
            case R.id.newAccont:
                startFragment(new SignUpFragment());
                break;
        }
    }

    private void startFragment(Fragment fragment) {
        startPage.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).addToBackStack(null).commit();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startPage.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);


    }
}
