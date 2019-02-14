package com.example.studentapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

public class register extends AppCompatActivity {

    private EditText userName, userMail, userPassword, userPasswordRepeat;
    private Button registerButton;
    private ProgressBar progressBar;
    private TextView loginText;
    private CheckBox checkBox;
    private ImageView image;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userName = findViewById(R.id.name);
        checkBox = findViewById(R.id.lecturer);
        userMail = findViewById(R.id.email);
        userPassword = findViewById(R.id.password);
        userPasswordRepeat = findViewById(R.id.password_repeat);
        registerButton = findViewById(R.id.register_btn);
        progressBar = findViewById(R.id.progress_bar);
        loginText = findViewById(R.id.login_text);
        image = findViewById(R.id.image);

        progressBar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();


        loginText.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                Intent intent = new Intent(register.this, login.class);
                startActivity(intent);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerButton.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                final String email = userMail.getText().toString();
                final String password = userPassword.getText().toString();
                final String passwordRepeat = userPasswordRepeat.getText().toString();
                final String name = userName.getText().toString();

                if(name.isEmpty() || email.isEmpty() || password.isEmpty() || passwordRepeat.isEmpty()){
                    showMessage("Verify all fields");
                    registerButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    CreateUserAccount(name, email, password);
                }
            }
        });

    }

    private void CreateUserAccount(final String name, String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    showMessage("Account authenticated!");
                    updateUserInfo(name, mAuth.getCurrentUser());

                    if(checkBox.isChecked()) {
                        boolean lecturer = true;
                        User user = new User(name, email, lecturer);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(mAuth.getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    showMessage("Account created!");
                                } else{
                                    showMessage("Account creation failed. " + task.getException().getMessage());
                                    registerButton.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    } else{
                        boolean lecturer = false;
                        User user = new User(name, email, lecturer);

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(mAuth.getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        showMessage("Account created!");
                                    } else{
                                        showMessage("Account creation failed. " + task.getException().getMessage());
                                        registerButton.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.VISIBLE);
                                    }
                                }
                            });
                        }
                } else{
                    showMessage("Account authentication failed. " + task.getException().getMessage());
                    registerButton.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void updateUserInfo(String name, FirebaseUser currentUser) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        currentUser.updateProfile(profileUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            updateUI();
                        }
                    }
                });
    }

    private void updateUI() {
        Intent home = new Intent(getApplicationContext(), studentHome.class);
        startActivity(home);
    }

    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }
}
