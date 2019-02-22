package com.example.studentapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class lecturerHome extends Fragment {
    private TextView homeLecturerName, homeLecturerEmail;
    private FirebaseAuth mAuth;

    public lecturerHome() {
        // Required empty public constructor
    }

    public static lecturerHome newInstance() {
        lecturerHome fragment = new lecturerHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lecturer_home, container, false);
        if(mAuth.getCurrentUser() != null) {
            homeLecturerName = v.findViewById(R.id.lecturer_home_name);
            homeLecturerEmail = v.findViewById(R.id.lecturer_home_email);
            homeLecturerName.setText(mAuth.getCurrentUser().getDisplayName());
            homeLecturerEmail.setText(mAuth.getCurrentUser().getEmail());

        }

        return v;
    }

}
