package com.example.studentapp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class studentHome extends Fragment {
    private TextView homeStudentName, homeStudentEmail;
    private FirebaseAuth mAuth;


    public studentHome() {
        // Required empty public constructor
    }

    public static studentHome newInstance() {
        studentHome fragment = new studentHome();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        View v = inflater.inflate(R.layout.fragment_student_home, container, false);

        homeStudentName = v.findViewById(R.id.student_home_text);
        homeStudentEmail = v.findViewById(R.id.student_home_email);
        homeStudentName.setText(mAuth.getCurrentUser().getDisplayName());
        homeStudentEmail.setText(mAuth.getCurrentUser().getEmail());

        return v;
    }
}
