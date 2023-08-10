package com.example.newproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {

    private List<String> studentIds;

    public StudentsAdapter(List<String> studentIds) {
        this.studentIds = studentIds;
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_students, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        String studentId = studentIds.get(position);

        holder.bind(studentId);

    }

    @Override
    public int getItemCount() {
        return studentIds.size();
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImageView;
        private TextView usernameTextView;



        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.student_profile_item);
            usernameTextView = itemView.findViewById(R.id.student_username_item);


        }

        public void bind(String studentId) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(studentId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String profileImageUrl = snapshot.child("profile").getValue(String.class);
                        String username = snapshot.child("userName").getValue(String.class);
                        Picasso.get().load(profileImageUrl).into(profileImageView);
                        usernameTextView.setText(username);
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
