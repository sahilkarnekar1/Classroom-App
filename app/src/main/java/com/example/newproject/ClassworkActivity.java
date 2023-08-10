package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ClassworkActivity extends AppCompatActivity {
    String classId;
    RecyclerView classworkRecView;
    private AssignmentAdapter assignmentAdapter;
    private List<Assignments> assignmentsList;
    private DatabaseReference assignmentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classwork);

classworkRecView = findViewById(R.id.classwork_recyclerView);

        classId = getIntent().getStringExtra("classId");
     assignmentRef = FirebaseDatabase.getInstance().getReference().child("classes")
                .child(classId) // Replace with your class ID
                .child("assignments");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        classworkRecView.setLayoutManager(linearLayoutManager);


        assignmentsList = new ArrayList<>();
        assignmentAdapter = new AssignmentAdapter(assignmentsList, this);

        // Use custom divider drawable (divider.xml)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        classworkRecView.addItemDecoration(dividerItemDecoration);

        classworkRecView.setAdapter(assignmentAdapter);



       DatabaseReference teacherRef= FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("members").child("teacher");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userId = currentUser.getUid();
        findViewById(R.id.classwork_fab_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            for (DataSnapshot teacherSnapshot : snapshot.getChildren()){
                                String teacherId = teacherSnapshot.getKey();
                                if (teacherId.equals(userId)){
                                    showBottomSheetDialog();
                                }else {
                                    Toast.makeText(ClassworkActivity.this, "You Are Only Student Of This Class", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });
    }
    private void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.classwork_bottom, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
    // Handle the click event for "Create Class"
    public void onClassWorkAssClick(View view) {
        // Handle the click event here, e.g., navigate to the create class activity
        Intent intent = new Intent(this, CreateAssignmentActivity.class);
        intent.putExtra("classId", classId);
        startActivity(intent);
    }

    private void loadAnnouncements() {
        assignmentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                assignmentsList.clear();
                for (DataSnapshot announcementSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : announcementSnapshot.getChildren()) {
                        Assignments announcement = userSnapshot.getValue(Assignments.class);
                        assignmentsList.add(announcement);
                    }

                }
                assignmentAdapter.notifyDataSetChanged();
                // Scroll the RecyclerView to the bottom
                if (!assignmentsList.isEmpty()) {
                    classworkRecView.scrollToPosition(assignmentsList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();

    }
}