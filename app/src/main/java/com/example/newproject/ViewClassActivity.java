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
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ViewClassActivity extends AppCompatActivity {

    TextView classNameTV,subjectTV;
    LinearLayout goToAddAnnouncement;
    private String classId;


    private DatabaseReference classRef;
    private LinearLayout backImage;

    private int[] backgroundImages = {
            R.drawable.image1,
            R.drawable.image2,
            R.drawable.image3,
            R.drawable.image4,
            R.drawable.image5,
            R.drawable.image6,
            R.drawable.image7


    };
    private ProgressDialog dialog;

    private DatabaseReference announcementsRef;
    private AnnouncementAdapter announcementAdapter;
    private List<Announcement> announcementList;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_class);
        getSupportActionBar().hide();
        classNameTV = findViewById(R.id.textClassNameViewClass);
        subjectTV=findViewById(R.id.textSectionSubjectRoomViewTV);
        goToAddAnnouncement=findViewById(R.id.goToAddAnnouncement);
        // Get the match ID from the intent
        classId = getIntent().getStringExtra("classId");


        announcementsRef = FirebaseDatabase.getInstance().getReference().child("classes")
                .child(classId) // Replace with your class ID
                .child("announcements");


        backImage = findViewById(R.id.backImage);


        dialog=new ProgressDialog(ViewClassActivity.this);
        dialog.setMessage("Loading");
        dialog.show();
        setRandomBackground();

         recyclerView = findViewById(R.id.viewClassRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        announcementList = new ArrayList<>();
        announcementAdapter = new AnnouncementAdapter(announcementList, this);

        // Use custom divider drawable (divider.xml)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(announcementAdapter);



        classRef = FirebaseDatabase.getInstance().getReference("classes").child(classId);
        classRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ClassInfo classInfo = dataSnapshot.getValue(ClassInfo.class);
                    if (classInfo != null) {
                        classNameTV.setText(classInfo.getClassName());
                        subjectTV.setText(classInfo.getSection() + " | " + classInfo.getSubject() + " | " + classInfo.getRoom());

                        dialog.dismiss();
                    }
                } else {
                    Toast.makeText(ViewClassActivity.this, "Match data does not exist", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        });

        goToAddAnnouncement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewClassActivity.this,MakeAnnouncementActivity.class);
                intent.putExtra("classId", classId);
                view.getContext().startActivity(intent);
            }
        });
    }

    private void setRandomBackground() {

        int randomIndex = getRandomNumber(backgroundImages.length);
        backImage.setBackgroundResource(backgroundImages[randomIndex]);

    }

    private int getRandomNumber(int maxExclusive) {
        Random random = new Random();
        return random.nextInt(maxExclusive);
    }

    private void loadAnnouncements() {
        announcementsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                announcementList.clear();
                for (DataSnapshot announcementSnapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot userSnapshot : announcementSnapshot.getChildren()) {
                        Announcement announcement = userSnapshot.getValue(Announcement.class);
                        announcementList.add(announcement);
                    }

                }
                announcementAdapter.notifyDataSetChanged();
                // Scroll the RecyclerView to the bottom
                if (!announcementList.isEmpty()) {
                    recyclerView.scrollToPosition(announcementList.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.action_Announcements) {
                Intent intent = new Intent(ViewClassActivity.this,ImpLinksActivity.class);
                intent.putExtra("classId", classId);
                startActivity(intent);
            } else if (itemId == R.id.action_classWork) {
                Intent intent = new Intent(ViewClassActivity.this,ClassworkActivity.class);
                intent.putExtra("classId", classId);
                startActivity(intent);
            } else if (itemId == R.id.action_people) {
                Intent intent = new Intent(ViewClassActivity.this,ClassPeopleActivity.class);
                intent.putExtra("classId", classId);
                startActivity(intent);
            }
            return true;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();

    }
}