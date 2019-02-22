package com.example.studentapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class lecturerGroup extends Fragment {

    private TextView groupText;
    private Button studentsAcceptBtn, studentsBtn, testBtn, lessonBtn, backBtn;
    private ListView studentList, studentAcceptedList;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapterStudent;
    private ArrayList<String> studentsArr = new ArrayList<>();
    private ArrayList<String> studentsAcceptedArr = new ArrayList<>();
    private ArrayList<String> studentsAcceptedKeyArr = new ArrayList<>();
    private String year, group, empty;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public lecturerGroup() {
        // Required empty public constructor
    }

    public static lecturerGroup newInstance() {
        lecturerGroup fragment = new lecturerGroup();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lecturer_group, container, false);

        pref = getContext().getSharedPreferences("com.example.studentApp", 0);
        editor = pref.edit();

        year = pref.getString("year", "Default year");
        group = pref.getString("group", "Default group");

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        groupText = v.findViewById(R.id.group_text);
        studentsAcceptBtn = v.findViewById(R.id.group_accept_btn);
        studentsBtn = v.findViewById(R.id.group_students_btn);
        testBtn = v.findViewById(R.id.group_test_btn);
        lessonBtn = v.findViewById(R.id.group_lesson_btn);
        backBtn = v.findViewById(R.id.back_button);
        studentList = v.findViewById(R.id.students);
        studentAcceptedList = v.findViewById(R.id.student_invite);
        groupText.setText(group);

        adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, studentsArr);
        studentList.setAdapter(adapter);

        adapterStudent = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, studentsAcceptedArr);
        studentAcceptedList.setAdapter(adapterStudent);

        mDatabase.child("year").child(year).child("group").child(group).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Log.d("LOL", dataSnapshot.toString());
                if(dataSnapshot.getKey().equals("studentInvites")){
                    String value = dataSnapshot.child("studentInvites").getKey();

                    if(studentsArr.contains(value)) {
                        studentsArr.add(value);
                        adapter.notifyDataSetChanged();
                    }
                }

                if(dataSnapshot.getKey().equals("students")){
                    String content = dataSnapshot.child("students").getValue().toString();

                    if(studentsAcceptedArr.contains(content)){
                        studentsAcceptedArr.add(content);
                        adapterStudent.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mDatabase.child("year").child(year).child("group").child(group).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.getKey().equals("studentInvites")){
                    String value = dataSnapshot.child("studentInvites").getKey();

                    if(studentsArr.contains(value)) {
                        studentsArr.add(value);
                        adapter.notifyDataSetChanged();
                    }
                }

                if(dataSnapshot.getKey().equals("students")){
                    String content = dataSnapshot.child("students").getValue().toString();

                    if(studentsAcceptedArr.contains(content)){
                        studentsAcceptedArr.add(content);
                        adapterStudent.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        studentsAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentsAcceptBtn.setVisibility(View.INVISIBLE);
                studentsBtn.setVisibility(View.INVISIBLE);
                testBtn.setVisibility(View.INVISIBLE);
                lessonBtn.setVisibility(View.INVISIBLE);
                studentAcceptedList.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);

                mDatabase.child("year").child(year).child("group").child(group).child("studentInvites").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Log.d("TAG", dataSnapshot.toString());
                        String value = dataSnapshot.getValue().toString();
                        String key = dataSnapshot.getKey();

                        if(!studentsAcceptedArr.contains(value)) {
                            studentsAcceptedArr.add(value);
                            adapterStudent.notifyDataSetChanged();

                            studentsAcceptedKeyArr.add(key);
                        }

                            studentAcceptedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    mDatabase.child("year")
                                                            .child(year)
                                                            .child("group")
                                                            .child(group)
                                                            .child("student")
                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                @Override
                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                    Log.d("TAG", "ARR: "+studentsAcceptedArr.toString());
                                                                    if (dataSnapshot.getKey().equals(studentsAcceptedArr.get(position))) {
                                                                        showMessage("Student exists");
                                                                    } else {
                                                                        mDatabase.child("year")
                                                                                .child(year)
                                                                                .child("group")
                                                                                .child(group)
                                                                                .child("student")
                                                                                .child(studentsAcceptedArr.get(position))
                                                                                .setValue("")
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if(task.isSuccessful()){
                                                                                    Log.d("TAG", studentsAcceptedArr.toString());
                                                                                    mDatabase.child("year")
                                                                                            .child(year)
                                                                                            .child("group")
                                                                                            .child(group)
                                                                                            .child("studentInvites")
                                                                                            .child(studentsAcceptedKeyArr.get(position))
                                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                            if(task.isSuccessful()){
                                                                                                FirebaseDatabase.getInstance().getReference()
                                                                                                        .child("Users")
                                                                                                        .child(key)
                                                                                                        .child("subjects")
                                                                                                        .child(pref.getString("year", "Year")+" "+pref.getString("group", "Group"))
                                                                                                        .setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if(task.isSuccessful()){
                                                                                                            showMessage("Student added to group");
                                                                                                            refresh();
                                                                                                        }
                                                                                                    }
                                                                                                });

                                                                                            }else{
                                                                                                showMessage("Unexpected error");
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }else{
                                                                                    showMessage("Something went wrong");
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                    adapterStudent.remove(studentsAcceptedArr.get(position));
                                                                }

                                                                @Override
                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                }
                                                            });
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("Add student to group?")
                                            .setNegativeButton("No", dialogClickListener)
                                            .setPositiveButton("Yes", dialogClickListener)
                                            .show();
                                }
                            });
                        }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        studentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentsAcceptBtn.setVisibility(View.INVISIBLE);
                studentsBtn.setVisibility(View.INVISIBLE);
                testBtn.setVisibility(View.INVISIBLE);
                lessonBtn.setVisibility(View.INVISIBLE);
                studentAcceptedList.setVisibility(View.INVISIBLE);
                studentList.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);

                mDatabase.child("year").child(year).child("group").child(group).child("student").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String key = dataSnapshot.getKey().toString();

                        if(!studentsArr.contains(key)){
                            studentsArr.add(key);
                            adapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                studentsAcceptBtn.setVisibility(View.VISIBLE);
                studentsBtn.setVisibility(View.VISIBLE);
                testBtn.setVisibility(View.VISIBLE);
                lessonBtn.setVisibility(View.VISIBLE);
                studentAcceptedList.setVisibility(View.INVISIBLE);
                studentList.setVisibility(View.INVISIBLE);
                backBtn.setVisibility(View.INVISIBLE);
            }
        });

        return v;
    }

    private void refresh(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, newInstance());
        fragmentTransaction.commit();
    }

    private void showMessage(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
    }
}
