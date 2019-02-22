package com.example.studentapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class home extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private studentSubject studentSubject;
    private studentHome studentHome;
    private lecturerSubject lecturerSubject;
    private lecturerHome lecturerHome;
    private RelativeLayout mainFrame;
    private BottomNavigationView bottomNavigationView;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        pref = getApplicationContext().getSharedPreferences("com.example.studentApp", 0);
        editor = pref.edit();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        bottomNavigationView = findViewById(R.id.bottom_nav);
        mainFrame = findViewById(R.id.main_frame);

        studentSubject = new studentSubject();
        studentHome = new studentHome();
        lecturerSubject = new lecturerSubject();
        lecturerHome = new lecturerHome();

        boolean lecturer = pref.getBoolean("Lecturer", false);

        if(lecturer == false) {
            setFragment(studentHome);
        } else {
            setFragment(lecturerHome);
        }

        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("year").exists()){
                    editor.putBoolean("firstYear", false).commit();
                } else{
                    editor.putBoolean("firstYear", true).commit();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                if(lecturer == false) {
                    switch (menuItem.getItemId()) {
                        case R.id.home_button:
                            finish();
                            startActivity(getIntent());
                            return true;
                        case R.id.subject:
                            setFragment(studentSubject);
                            return true;
                        case R.id.logout:
                            mAuth.signOut();
                            updateLoginUI();
                            editor.clear();
                            editor.commit();
                            return true;

                        default:
                            return false;

                    }
                } else{
                    switch (menuItem.getItemId()) {
                        case R.id.home_button:
                            finish();
                            startActivity(getIntent());
                            return true;
                        case R.id.subject:
                            setFragment(lecturerSubject);
                            return true;
                        case R.id.logout:
                            mAuth.signOut();
                            updateLoginUI();
                            editor.clear();
                            editor.commit();
                            return true;

                        default:
                            return false;

                    }
                }
            }
        });
    }

    private void setFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, fragment);
        fragmentTransaction.detach(fragment).attach(fragment);
        fragmentTransaction.commit();
    }

    private void updateLoginUI() {
        Intent login = new Intent(getApplicationContext(), login.class);
        startActivity(login);
    }
}

