package com.example.studentapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.time.Year;
import java.util.ArrayList;

import static android.R.layout.simple_list_item_1;

public class lecturerSubject extends Fragment {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private RelativeLayout layout;
    private ListView yearsList, groupsList;
    private ProgressBar progressBar;
    private TextView addYear, addNextYear, addGroup, addNextGroup;
    private Button addYearBtn, addGroupBtn;
    private EditText addYearEdit;
    private String newYear, nextYear, newGroup, nextGroup;
    private ArrayList<String> yearsArr = new ArrayList<>();
    private ArrayList<String> groupsArr = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapterGroups;
    private lecturerSubject lecturerSubject;
    private lecturerGroup lecturerGroup;
    private boolean firstYear, firstGroup;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    public lecturerSubject() {
        // Required empty public constructor
    }

    public static lecturerSubject newInstance() {
        lecturerSubject fragment = new lecturerSubject();
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

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        View v = inflater.inflate(R.layout.fragment_lecturer_subject, container, false);

        layout = v.findViewById(R.id.lecturer_subject_layout);
        addYear = v.findViewById(R.id.add_year_text);
        addNextYear = v.findViewById(R.id.add_next_year_text);
        addGroup = v.findViewById(R.id.add_group_text);
        addNextGroup = v.findViewById(R.id.add_next_group_text);
        addYearBtn = v.findViewById(R.id.add_year_button);
        addGroupBtn = v.findViewById(R.id.add_group_button);
        addYearEdit = v.findViewById(R.id.add_year_edit);
        progressBar = v.findViewById(R.id.add_progress);
        yearsList = v.findViewById(R.id.years_list);
        groupsList = v.findViewById(R.id.group_list);

        lecturerGroup = new lecturerGroup();

        adapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, yearsArr);
        yearsList.setAdapter(adapter);

        adapterGroups = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, groupsArr);
        groupsList.setAdapter(adapterGroups);

        mDatabase.child("year").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String value = dataSnapshot.getKey().toString();

                if(!yearsArr.contains(value)) {
                    yearsArr.add(value);
                    adapter.notifyDataSetChanged();
                }

                if(dataSnapshot.exists()){
                    addYear.setVisibility(View.INVISIBLE);
                    addNextYear.setVisibility(View.VISIBLE);
                } else{
                    addYear.setVisibility(View.VISIBLE);
                    addNextYear.setVisibility(View.INVISIBLE);
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

        addYearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addYearBtn.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                yearsList.setVisibility(View.VISIBLE);
                newYear = addYearEdit.getText().toString();
                mDatabase.child("year").child(newYear).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            showMessage("Year with given name exists.");
                            addYearBtn.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                        } else{
                            mDatabase.child("year").child(newYear).child("group").setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        addYearBtn.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.INVISIBLE);
                                        addYearEdit.setText("");
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
        });

        yearsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                addYear.setVisibility(View.INVISIBLE);
                addNextYear.setVisibility(View.INVISIBLE);
                addYearBtn.setVisibility(View.INVISIBLE);
                yearsList.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                addGroupBtn.setVisibility(View.VISIBLE);
                groupsList.setVisibility(View.VISIBLE);

                newGroup = addYearEdit.getText().toString();
                editor.putInt("yearPosition", position).commit();

                mDatabase.child("year").child(yearsArr.get(position)).child("group").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        String group = dataSnapshot.getKey().toString();

                        if(!groupsArr.contains(group)){
                            groupsArr.add(group);
                            adapterGroups.notifyDataSetChanged();
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

                addGroupBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addGroupBtn.setVisibility(View.INVISIBLE);
                        progressBar.setVisibility(View.VISIBLE);

                        newGroup = addYearEdit.getText().toString();

                        mDatabase.child("year").child(yearsArr.get(position)).child("group").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(groupsArr.contains(newGroup)){
                                    addGroupBtn.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.INVISIBLE);
                                    showMessage("Group with given name exists.");
                                }else{
                                    mDatabase.child("year").child(yearsArr.get(position)).child("group").child(newGroup).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                addGroupBtn.setVisibility(View.VISIBLE);
                                                progressBar.setVisibility(View.INVISIBLE);
                                                addGroup.setVisibility(View.INVISIBLE);
                                                addNextGroup.setVisibility(View.VISIBLE);
                                                addYearEdit.setText("");
                                            } else{
                                                showMessage("Something went wrong.");
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
                });

                mDatabase.child("year").child(yearsArr.get(position)).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if(dataSnapshot.getValue().toString().equals("")){
                            addGroup.setVisibility(View.VISIBLE);
                            addNextGroup.setVisibility(View.INVISIBLE);
                        }else{
                            addGroup.setVisibility(View.INVISIBLE);
                            addNextGroup.setVisibility(View.VISIBLE);
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

        yearsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                mDatabase.child("year").child(yearsArr.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            adapter.remove(yearsArr.get(position));
                                            showMessage("Year successfully removed.");
                                        }
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete this year and groups?")
                        .setNegativeButton("No", dialogClickListener)
                        .setPositiveButton("Yes", dialogClickListener)
                        .show();
                return false;
            }
        });

        groupsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editor.putString("group", groupsArr.get(position)).commit();
                editor.putString("year", yearsArr.get(pref.getInt("yearPosition", 0))).commit();
                yearsArr.clear();
                groupsArr.clear();

                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.main_frame, lecturerGroup);
                transaction.commit();
            }
        });

        groupsList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                mDatabase.child("year").child(yearsArr.get(position)).child("group").child(groupsArr.get(position)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            adapterGroups.remove(groupsArr.get(position));
                                            adapter.remove(yearsArr.get(position));
                                            showMessage("Group successfully removed.");
                                        }
                                    }
                                });
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Do you want to delete this group?")
                        .setNegativeButton("No", dialogClickListener)
                        .setPositiveButton("Yes", dialogClickListener)
                        .show();

                return false;
            }
        });

        return v;
    }

    private void showMessage(String text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_LONG).show();
    }
}
