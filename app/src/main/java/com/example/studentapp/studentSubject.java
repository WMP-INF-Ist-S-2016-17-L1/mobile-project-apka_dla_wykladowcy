package com.example.studentapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class studentSubject extends Fragment {
    private TextView lecturerText, lecturerGroupsText;
    private EditText editText;
    private Button findLecturerBtn;
    private ListView lecturerList, lecturerYearsList, lecturerGroupsList, subjectsList;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private String lecturerEmail;
    private studentGroup studentGroup;

    private List<List<String>> lecturerGroupsArrayList = new ArrayList<>();
    private ArrayList<String> lecturerGroupsArr = new ArrayList<>();
    private ArrayList<String> lecturerYearsArr = new ArrayList<>();
    private ArrayList<String> lecturerArr = new ArrayList<>();
    private ArrayList<String> subjectsArr = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapterSubjects;
    private ArrayAdapter<String> adapterLecturerYears;
    private ArrayAdapter<String> adapterLecturerGroups;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public studentSubject() {
        // Required empty public constructor
    }

    public static studentSubject newInstance() {
        studentSubject fragment = new studentSubject();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pref = getContext().getSharedPreferences("com.example.studentApp", 0);
        editor = pref.edit();

        studentGroup = new studentGroup();

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        View v = inflater.inflate(R.layout.fragment_student_subject, container, false);

        lecturerText = v.findViewById(R.id.lecturer_text);
        lecturerGroupsText = v.findViewById(R.id.lecturer_groups_text);
        editText = v.findViewById(R.id.find_lecturer);
        findLecturerBtn = v.findViewById(R.id.find_lecturer_button);
        lecturerList = v.findViewById(R.id.find_lecturer_list);
        lecturerYearsList = v.findViewById(R.id.find_lecturer_years_list);
        lecturerGroupsList = v.findViewById(R.id.find_lecturer_groups_list);
        subjectsList = v.findViewById(R.id.subjects_list);

        adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, lecturerArr);
        lecturerList.setAdapter(adapter);

        adapterSubjects = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, subjectsArr);
        subjectsList.setAdapter(adapterSubjects);

        adapterLecturerGroups = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, lecturerGroupsArr);
        lecturerGroupsList.setAdapter(adapterLecturerGroups);

        adapterLecturerYears = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_list_item_1, lecturerYearsArr);
        lecturerYearsList.setAdapter(adapterLecturerYears);

        lecturerList.setVisibility(View.INVISIBLE);
        lecturerYearsList.setVisibility(View.INVISIBLE);
        lecturerGroupsList.setVisibility(View.INVISIBLE);
        subjectsList.setVisibility(View.VISIBLE);


        mDatabase.child(mAuth.getCurrentUser().getUid()).child("subjects").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String key = dataSnapshot.getKey().toString();

                if (!subjectsArr.contains(key)) {
                    subjectsArr.add(key);
                    adapterSubjects.notifyDataSetChanged();
                }

                subjectsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        editor.putString("studentGroup", subjectsArr.get(position)).commit();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_frame, studentGroup);
                        transaction.commit();
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

        findLecturerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lecturerArr.clear();
                findLecturerBtn.setVisibility(View.INVISIBLE);

                lecturerEmail = editText.getText().toString();

                mDatabase.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getValue(User.class).getLecturer()){
                            String key = dataSnapshot.getKey();
                            mDatabase.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    String email = dataSnapshot.getValue(User.class).getEmail();
                                    if(email.compareToIgnoreCase(lecturerEmail) == 0){
                                        if(!lecturerArr.contains(email)) {
                                            lecturerArr.add(email);
                                            adapter.notifyDataSetChanged();
                                            List<String> list = new ArrayList<String>();
                                            list.add(key);
                                            list.add(email);
                                            lecturerGroupsArrayList.add(list);
                                        }

                                        subjectsList.setVisibility(View.INVISIBLE);
                                        lecturerList.setVisibility(View.VISIBLE);
                                        editText.setText("");

                                        editor.putString("email", email).commit();
                                        showMessage("Lecturer found");

                                        lecturerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                lecturerYearsArr.clear();

                                                lecturerText.setVisibility(View.INVISIBLE);
                                                lecturerGroupsText.setVisibility(View.VISIBLE);
                                                editText.setVisibility(View.INVISIBLE);
                                                findLecturerBtn.setVisibility(View.INVISIBLE);

                                                lecturerList.setVisibility(View.INVISIBLE);
                                                lecturerYearsList.setVisibility(View.VISIBLE);

                                                mDatabase.child(lecturerGroupsArrayList.get(position).get(0))
                                                        .child("year")
                                                        .addChildEventListener(new ChildEventListener() {
                                                            @Override
                                                            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                                String value = dataSnapshot.getKey().toString();

                                                                if(!lecturerYearsArr.contains(value)){
                                                                    lecturerYearsArr.add(value);
                                                                    adapterLecturerYears.notifyDataSetChanged();
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

                                                lecturerYearsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        subjectsList.setVisibility(View.INVISIBLE);
                                                        lecturerList.setVisibility(View.INVISIBLE);
                                                        lecturerYearsList.setVisibility(View.INVISIBLE);
                                                        lecturerGroupsList.setVisibility(View.VISIBLE);

                                                        mDatabase.child(lecturerGroupsArrayList.get(0).get(0))
                                                                .child("year")
                                                                .child(lecturerYearsArr.get(position))
                                                                .child("group")
                                                                .addChildEventListener(new ChildEventListener() {
                                                                    @Override
                                                                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                                                        String value = dataSnapshot.getKey();

                                                                        if(!lecturerGroupsArr.contains(value)){
                                                                            lecturerGroupsArr.add(value);
                                                                            adapterLecturerGroups.notifyDataSetChanged();
                                                                            editor.putInt("position", position).commit();
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

                                                        lecturerGroupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                            @Override
                                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        switch (which){
                                                                            case DialogInterface.BUTTON_POSITIVE:
                                                                                mDatabase.child(lecturerGroupsArrayList.get(0).get(0))
                                                                                        .child("year")
                                                                                        .child(lecturerYearsArr.get(pref.getInt("position", 0)))
                                                                                        .child("group")
                                                                                        .child(lecturerGroupsArr.get(position))
                                                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                            @Override
                                                                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                Log.d("TAG", dataSnapshot.toString());
                                                                                                String value = dataSnapshot.toString();
                                                                                                if(value.equals("")){
                                                                                                    mDatabase.child(lecturerGroupsArrayList.get(0).get(0))
                                                                                                            .child("year")
                                                                                                            .child(lecturerYearsArr.get(pref.getInt("position", 0)))
                                                                                                            .child("group")
                                                                                                            .child(lecturerGroupsArr.get(position))
                                                                                                            .child("studentInvites")
                                                                                                            .child(mAuth.getCurrentUser().getUid())
                                                                                                            .setValue(mAuth.getCurrentUser().getDisplayName().toString())
                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                @Override
                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                    if(task.isSuccessful()){
                                                                                                                        showMessage("Completed");
                                                                                                                        refresh();
                                                                                                                    }else{
                                                                                                                        showMessage("Unexpected error");
                                                                                                                    }
                                                                                                                }
                                                                                                            });
                                                                                                }else {
                                                                                                    mDatabase.child(lecturerGroupsArrayList.get(0).get(0))
                                                                                                            .child("year")
                                                                                                            .child(lecturerYearsArr.get(pref.getInt("position", 0)))
                                                                                                            .child("group")
                                                                                                            .child(lecturerGroupsArr.get(position))
                                                                                                            .child("studentInvites")
                                                                                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                                                                @Override
                                                                                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                                                                                    if(dataSnapshot.hasChild(mAuth.getCurrentUser().getUid())){
                                                                                                                        showMessage("Request exists");
                                                                                                                    } else{
                                                                                                                        mDatabase.child(lecturerGroupsArrayList.get(0).get(0))
                                                                                                                                .child("year")
                                                                                                                                .child(lecturerYearsArr.get(pref.getInt("position", 0)))
                                                                                                                                .child("group")
                                                                                                                                .child(lecturerGroupsArr.get(position))
                                                                                                                                .child("studentInvites")
                                                                                                                                .child(mAuth.getCurrentUser().getUid())
                                                                                                                                .setValue(mAuth.getCurrentUser().getDisplayName())
                                                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        if(task.isSuccessful()){
                                                                                                                                            showMessage("Completed");
                                                                                                                                            editor.clear();
                                                                                                                                            refresh();
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });
                                                                                                                    }
                                                                                                                }

                                                                                                                @Override
                                                                                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                                                                                }
                                                                                                            });
                                                                                                }
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
                                                                builder.setMessage("Lecturer has to accept your request in order to join this group. Do you want to proceed?")
                                                                        .setNegativeButton("No", dialogClickListener)
                                                                        .setPositiveButton("Yes", dialogClickListener)
                                                                        .show();

                                                            }
                                                        });
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                        findLecturerBtn.setVisibility(View.VISIBLE);
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
        return v;
    }

    private void refresh(){
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame, newInstance());
        fragmentTransaction.commit();
    }

    private void showMessage(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }
}
