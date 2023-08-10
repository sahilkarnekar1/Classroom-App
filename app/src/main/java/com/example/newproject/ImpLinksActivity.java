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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ImpLinksActivity extends AppCompatActivity {

    private LinearLayout hiddenLayout;
    private EditText etLink,etDesc;
    private Button btnPostLink;
    private RecyclerView linksRecView;
    private LinksAdapter linksAdapter;
    private List<LinksModel> linksModelList;

   String classId;
   DatabaseReference linksRefRecRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imp_links);

        classId = getIntent().getStringExtra("classId");

        hiddenLayout=findViewById(R.id.hiddenLinearLayout);
        etLink=findViewById(R.id.editTextLink);
        etDesc=findViewById(R.id.etDescLink);
        btnPostLink=findViewById(R.id.buttonPostLink);
        linksRecView=findViewById(R.id.impLinksRecView);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference().child("classes").child(classId);

        teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    ClassInfo classInfo = snapshot.getValue(ClassInfo.class);
                    String teacherId=classInfo.getUserId();
                    if (teacherId.equals(userId)){
                        hiddenLayout.setVisibility(View.VISIBLE);
                    }else {
                        hiddenLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        btnPostLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String link = etLink.getText().toString();
                String desc = etDesc.getText().toString();
                if (link.isEmpty() || desc.isEmpty()){
                    Toast.makeText(ImpLinksActivity.this, "Please fill all !", Toast.LENGTH_SHORT).show();
                }else {


                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                String linkId = sdf.format(new Date());
                LinksModel linksModel = new LinksModel(link,desc);
                FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("Links").child(linkId).setValue(linksModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ImpLinksActivity.this, "Link Posted !", Toast.LENGTH_SHORT).show();

                    }
                });
                }
            }
        });


        linksRefRecRef = FirebaseDatabase.getInstance().getReference().child("classes")
                .child(classId) // Replace with your class ID
                .child("Links");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linksRecView.setLayoutManager(linearLayoutManager);


        linksModelList = new ArrayList<>();
        linksAdapter = new LinksAdapter(linksModelList, this);

        // Use custom divider drawable (divider.xml)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        linksRecView.addItemDecoration(dividerItemDecoration);

        linksRecView.setAdapter(linksAdapter);


    }
    private void loadAnnouncements() {
        linksRefRecRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                linksModelList.clear();
                for (DataSnapshot announcementSnapshot : dataSnapshot.getChildren()) {
                        LinksModel linksModel = announcementSnapshot.getValue(LinksModel.class);
                        linksModelList.add(linksModel);

                }
                linksAdapter.notifyDataSetChanged();
                // Scroll the RecyclerView to the bottom
                if (!linksModelList.isEmpty()) {
                    linksRecView.scrollToPosition(linksModelList.size() - 1);
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