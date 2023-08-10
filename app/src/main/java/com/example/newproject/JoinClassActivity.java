package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class JoinClassActivity extends AppCompatActivity {
    private EditText editTextClassId;
    private Button btnJoinClass;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_class);

        editTextClassId = findViewById(R.id.editTextClassId);
        btnJoinClass = findViewById(R.id.btnJoinClass);

        // Initialize Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnJoinClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinClass();
            }
        });
    }
    private void joinClass() {
        // Get the class ID entered by the user
        String classId = editTextClassId.getText().toString().trim();

        // Validate the class ID (you can add more validation as needed)
        if (classId.isEmpty()) {
            Toast.makeText(this, "Please enter a valid Class ID", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current user's ID (assuming the user is authenticated)
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User not authenticated, handle this case accordingly
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        // Check if the class exists in the database
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("classes").child(classId);
        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // The class exists in the database
                    databaseReference.child("users").child(userId).child("classes").child(classId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                // User is already a member
                                Toast.makeText(JoinClassActivity.this, "You are already a member of this class", Toast.LENGTH_SHORT).show();
                                finish();

                            }else {
                                // Add the user as a student member of the class
                                databaseReference.child("users").child(userId).child("classes").child(classId).setValue(classId)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // User joined the class successfully
                                                databaseReference.child("classes").child(classId).child("members").child("students").child(userId).setValue(userId);
                                                Toast.makeText(JoinClassActivity.this, "Joined the class successfully", Toast.LENGTH_SHORT).show();
                                                finish(); // Close this activity and go back to the previous activity
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                // Failed to join the class
                                                Toast.makeText(JoinClassActivity.this, "Failed to join the class", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } else {
                    // Class with the given ID does not exist in the database
                    Toast.makeText(JoinClassActivity.this, "Class not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Error occurred while reading from the database
                Toast.makeText(JoinClassActivity.this, "Failed to read class data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
