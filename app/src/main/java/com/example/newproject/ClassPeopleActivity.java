package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClassPeopleActivity extends AppCompatActivity implements  C_Box_Listener{

    CircleImageView teacherImageView;
    TextView teacherUsernameTV;
    RecyclerView recyclerView;
    String classId;
    Button attendanceBtn;
    List<String> studentIds;
    String userId11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_people);

        teacherImageView = findViewById(R.id.people_T_profile);
        teacherUsernameTV = findViewById(R.id.peopel_teacher_userName);
        recyclerView = findViewById(R.id.peopleRecyclerview);
        attendanceBtn=findViewById(R.id.attendanceBtn);

        classId = getIntent().getStringExtra("classId");
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userId11= firebaseUser.getUid();
        studentIds = new ArrayList<>();


        DatabaseReference studentsRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("members").child("students");
        studentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentIds.clear(); // Clear the list before adding new data
                for (DataSnapshot studentSnapshot : dataSnapshot.getChildren()) {
                    String studentId = studentSnapshot.getKey();
                    studentIds.add(studentId);
                }

                StudentsAdapter adapter = new StudentsAdapter(ClassPeopleActivity.this, studentIds,ClassPeopleActivity.this,classId);
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

        DatabaseReference teac = FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("members").child("teacher").getRef();
        teac.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot teacherSnapshot : snapshot.getChildren()){
                        String teacherId = teacherSnapshot.getKey();
                        if (userId11.equals(teacherId)){
                            attendanceBtn.setVisibility(View.VISIBLE);

                        }else {
                            attendanceBtn.setVisibility(View.GONE);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        attendanceBtn.setOnClickListener(v -> markAttendance());
    }

    private void markAttendance() {
        // Get the list of selected student IDs from the adapter
        List<String> selectedStudents = ((StudentsAdapter) recyclerView.getAdapter()).getSelectedStudents();

        // Mark attendance for selected students in the database
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("attendance");
        String currentDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).format(new Date());

        for (String studentId : selectedStudents) {
            attendanceRef.child(currentDate).child(studentId).setValue(true);
        }

        Toast.makeText(this, "Attendance marked for selected students", Toast.LENGTH_SHORT).show();
        calculateAttendancePercentage();
    }

    @Override
    public void onCBoxChange(List<String> studentList) {

    }
    private void calculateAttendancePercentage() {
        DatabaseReference attendanceRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("attendance");
        attendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (String studentId : studentIds) {
                    int totalDays = (int) dataSnapshot.getChildrenCount();
                    int presentDays = 0;
                    int absentDays = 0;
                    for (DataSnapshot dateSnapshot : dataSnapshot.getChildren()) {
                        if (dateSnapshot.child(studentId).exists() && dateSnapshot.child(studentId).getValue(Boolean.class)) {
                            presentDays++;
                        }else{
                            absentDays++;
                        }
                    }
                    double attendancePercentage = (double) presentDays / totalDays * 100;
                    // Set the attendance data (percentage, present count, absent count) in the adapter
                    ((StudentsAdapter) recyclerView.getAdapter()).setAttendanceData(studentId, attendancePercentage, presentDays, absentDays);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Calculate attendance percentages whenever the activity is resumed
        calculateAttendancePercentage();
    }

}