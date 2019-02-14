package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
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
    boolean lecturer;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = findViewById(R.id.email);
        userPassword = findViewById(R.id.password);
        registerText = findViewById(R.id.register_text);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progress_bar);
        image = findViewById(R.id.image);
        home = new Intent(this, studentHome.class);

        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();


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
        mAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    loginBtn.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);

                    mDatabase = FirebaseDatabase.getInstance().getReference();


                    mDatabase.child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            User data = dataSnapshot.getValue(User.class);
                            lecturer = data.lecturer;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    if(lecturer){
                        updateLecturerUI();
                    } else{
                        updateStudentUI();
                    }
                } else{
                    showMessage(task.getException().getMessage());
                }
            }
        });

    }

    private void updateStudentUI() {
        Intent studentHome = new Intent(getApplicationContext(), studentHome.class);
        startActivity(studentHome);
    }

    private void updateLecturerUI() {
        Intent lecturerHome = new Intent(getApplicationContext(), lecturerHome.class);
        startActivity(lecturerHome);
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() != null){
            mDatabase = FirebaseDatabase.getInstance().getReference();

            mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User data = dataSnapshot.getValue(User.class);
                    lecturer = data.getLecturer();
                    Log.d("TAG", Boolean.toString(lecturer));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            lecturer = true;
            if(lecturer == true){
                updateLecturerUI();
            } else{
                updateStudentUI();
            }
        }
    }
}
