package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClassPeopleActivity extends AppCompatActivity {

    CircleImageView teacherImageView;
    TextView teacherUsernameTV;
    RecyclerView recyclerView;
    String classId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_people);

        teacherImageView = findViewById(R.id.people_T_profile);
        teacherUsernameTV = findViewById(R.id.peopel_teacher_userName);
        recyclerView = findViewById(R.id.peopleRecyclerview);

        classId = getIntent().getStringExtra("classId");
        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("members").child("students");
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> studentIds = new ArrayList<>();
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentId = studentSnapshot.getKey();
                    studentIds.add(studentId);
                }

                StudentsAdapter adapter = new StudentsAdapter(studentIds);
                recyclerView.setLayoutManager(new LinearLayoutManager(ClassPeopleActivity.this));
                // Use custom divider drawable (divider.xml)
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(ClassPeopleActivity.this, LinearLayoutManager.VERTICAL);
                dividerItemDecoration.setDrawable(ContextCompat.getDrawable(ClassPeopleActivity.this, R.drawable.divider));
                recyclerView.addItemDecoration(dividerItemDecoration);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });


        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("members").child("teacher");
        teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot teacherSnapshot : dataSnapshot.getChildren()) {
                        String teacherId = teacherSnapshot.getKey(); // Get the teacher's user ID
                        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(teacherId);
                        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String teacherUsername = snapshot.child("userName").getValue(String.class);
                                    teacherUsernameTV.setText(teacherUsername);
                                }
                                if (snapshot.child("profile").exists()){
                                    String teacherProfile = snapshot.child("profile").getValue(String.class);
                                    Picasso.get().load(teacherProfile).into(teacherImageView);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle onCancelled
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }
}