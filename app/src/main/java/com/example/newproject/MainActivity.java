package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;


    private RecyclerView recyclerView;
    private List<ClassInfo> enrolledClassesList;
    private ClassAdapter classAdapter;

    private int[] backgroundImages = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7


            // Add more drawable resource IDs for images here...
    };

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        recyclerView = findViewById(R.id.recyclerView);

        enrolledClassesList = new ArrayList<>();
        classAdapter = new ClassAdapter(enrolledClassesList, backgroundImages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use custom divider drawable (divider.xml)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(classAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        // Initialize firebase user
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();


        findViewById(R.id.fab_plus).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
    }
    private void showBottomSheetDialog() {
        View view = getLayoutInflater().inflate(R.layout.layout_bottom_sheet, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
    // Handle the click event for "Create Class"
    public void onCreateClassClick(View view) {
        // Handle the click event here, e.g., navigate to the create class activity
        Intent intent = new Intent(this, CreateClassActivity.class);
        startActivity(intent);
    }

    // Handle the click event for "Join Class"
    public void onJoinClassClick(View view) {
        // Handle the click event here, e.g., show a dialog for entering the class code
        Intent intent = new Intent(this, JoinClassActivity.class);
        startActivity(intent);
    }
    public void onAiAssistantClick(View view) {
        // Handle the click event here, e.g., show a dialog for entering the class code
        Intent intent = new Intent(this, AssistantActivity.class);
        startActivity(intent);
    }

    private void fetchEnrolledClasses(String userId) {
        dialog=new ProgressDialog(MainActivity.this);
        dialog.setMessage("Loading");
        dialog.show();
        DatabaseReference userClassesRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("classes");

        userClassesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                enrolledClassesList.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot classSnapshot : dataSnapshot.getChildren()) {
                        String classId = classSnapshot.getKey();
                        fetchClassDetails(classId);
                    }
                }else {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Please Create or Join Classes", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void fetchClassDetails(String classId) {
        DatabaseReference classRef = FirebaseDatabase.getInstance().getReference("classes").child(classId);

        classRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ClassInfo classInfo = dataSnapshot.getValue(ClassInfo.class);
                if (classInfo != null) {
                    enrolledClassesList.add(classInfo);
                    classAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.toString(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check if the user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            fetchEnrolledClasses(userId);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_action_profile) {
            // Handle the Profile option click here.
            Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_action_logout) {
            // Handle the Logout option click here.
            firebaseAuth.signOut();
            Intent intent = new Intent(MainActivity.this,SigninActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Logout Successful", Toast.LENGTH_SHORT).show();
            finish();
            // Add your logout logic here (e.g., sign out the user).
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}