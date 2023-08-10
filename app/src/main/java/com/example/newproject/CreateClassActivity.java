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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateClassActivity extends AppCompatActivity {

    private EditText editTextClassName;
    private EditText editTextSection;
    private EditText editTextSubject;
    private EditText editTextRoom;
    private Button btnCreateClass;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_class);

        editTextClassName = findViewById(R.id.editTextClassName);
        editTextSection = findViewById(R.id.editTextSection);
        editTextSubject = findViewById(R.id.editTextSubject);
        editTextRoom = findViewById(R.id.editTextRoom);
        btnCreateClass = findViewById(R.id.btnCreateClass);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClass();
            }
        });
    }

    private void createClass() {
        // Get the values entered by the user
        String className = editTextClassName.getText().toString().trim();
        String section = editTextSection.getText().toString().trim();
        String subject = editTextSubject.getText().toString().trim();
        String room = editTextRoom.getText().toString().trim();

        // Validate the input fields (you can add more validation as needed)
        if (className.isEmpty() || section.isEmpty() || subject.isEmpty() || room.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
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

        // Generate a unique key for the class using push()

        DatabaseReference classRef = databaseReference.child("classes").push();
        String classId = classRef.getKey();

        // Create a Class object to store the class data
        ClassInfo classInfo = new ClassInfo(classId, className, section, subject, room, userId);

        // Save the class data to Firebase Realtime Database
        classRef.setValue(classInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Class data saved successfully
                        databaseReference.child("users").child(userId).child("classes").child(classId).setValue(classInfo.getClassId());

                        databaseReference.child("classes").child(classId).child("members").child("teacher").child(userId).setValue(userId);

                        Toast.makeText(CreateClassActivity.this, "Class created successfully", Toast.LENGTH_SHORT).show();
                        finish(); // Close this activity and go back to the previous activity
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Failed to save class data
                        Toast.makeText(CreateClassActivity.this, "Failed to create class", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}
