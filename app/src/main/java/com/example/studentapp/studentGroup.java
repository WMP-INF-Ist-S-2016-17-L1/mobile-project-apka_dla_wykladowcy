package com.example.studentapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class studentGroup extends Fragment {
    private TextView text;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public studentGroup() {
        // Required empty public constructor
    }

    public static studentGroup newInstance() {
        studentGroup fragment = new studentGroup();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_student_group, container, false);
        pref = getContext().getSharedPreferences("com.example.studentApp", 0);
        editor = pref.edit();

        text = v.findViewById(R.id.group_text);

        text.setText(pref.getString("studentGroup", "Group"));

        return v;
    }

}
