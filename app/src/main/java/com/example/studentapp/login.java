package com.example.studentapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class login extends AppCompatActivity {

    private EditText userMail, userPassword;
    private TextView registerText;
    private Button loginBtn;
    private ProgressBar progressBar;
    private ImageView image;
    private Intent home;
    private boolean lecturer;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pref = getApplicationContext().getSharedPreferences("com.example.studentApp", 0);
        editor = pref.edit();
        editor.clear().commit();

        userMail = findViewById(R.id.email);
        userPassword = findViewById(R.id.password);
        registerText = findViewById(R.id.register_text);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        image = findViewById(R.id.image);
        home = new Intent(this, com.example.studentapp.home.class);

        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser() != null){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.keepSynced(true);

            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User data = dataSnapshot.getValue(User.class);
                    lecturer = data.getLecturer();
                    editor.putBoolean("Lecturer", lecturer).commit();
                    updateUI();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.INVISIBLE);

                final String mail = userMail.getText().toString();
                final String password = userPassword.getText().toString();

                if(mail.isEmpty() || password.isEmpty()){
                    showMessage("Please verify all fields!");
                } else{
                    singIn(mail, password);
                }
            }
        });

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), register.class);
                startActivity(intent);
            }
        });
    }

    private void singIn(String mail, String password) {
        editor.clear().commit();
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loginBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    mDatabase = FirebaseDatabase.getInstance().getReference();
                    mDatabase.keepSynced(true);

                    mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User data = dataSnapshot.getValue(User.class);
                            lecturer = data.getLecturer();
                            editor.putBoolean("Lecturer", lecturer).commit();
                            updateUI();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else{
                    showMessage(task.getException().getMessage());
                    loginBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void getLecturer(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.keepSynced(true);

        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User data = dataSnapshot.getValue(User.class);
                lecturer = data.getLecturer();
                editor.putBoolean("Lecturer", lecturer).commit();
                boolean sth = pref.getBoolean("Lecturer", false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI() {
        Intent home = new Intent(getApplicationContext(), home.class);
        startActivity(home);
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
