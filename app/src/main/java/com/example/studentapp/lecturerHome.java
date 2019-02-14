package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class lecturerHome extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Button logoutBtn;
    private TextView text1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_home);

        text1 = findViewById(R.id.text1);

        mAuth = FirebaseAuth.getInstance();

        logoutBtn = findViewById(R.id.logout_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                updateLoginUI();
            }
        });
    }

    private void updateLoginUI() {
        Intent login = new Intent(getApplicationContext(), login.class);
        startActivity(login);
    }
}
