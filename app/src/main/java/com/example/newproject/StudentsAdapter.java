package com.example.newproject;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentViewHolder> {
Context context;
    private List<String> studentIds;
    C_Box_Listener c_box_listener;
    String classId;
    List<String> arrayList_0 = new ArrayList<>();
    private Map<String, Double> attendancePercentages = new HashMap<>();
    private Map<String, Integer> presentCounts = new HashMap<>(); // Store present counts
    private Map<String, Integer> absentCounts = new HashMap<>();
    public void setAttendanceData(String studentId, double percentage, int presentCount, int absentCount) {
        attendancePercentages.put(studentId, percentage);
        presentCounts.put(studentId, presentCount);
        absentCounts.put(studentId, absentCount);
        notifyDataSetChanged(); // Refresh the adapter to update the UI
    }


    public StudentsAdapter(Context context, List<String> studentIds, C_Box_Listener c_box_listener,String classId) {
        this.context = context;
        this.studentIds = studentIds;
        this.c_box_listener = c_box_listener;
        this.classId = classId;
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
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId= firebaseUser.getUid();



        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(studentId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profileImageUrl = snapshot.child("profile").getValue(String.class);
                    String username = snapshot.child("userName").getValue(String.class);
                    Picasso.get().load(profileImageUrl).into(holder.profileImageView);
                    holder.usernameTextView.setText(username);

                    DatabaseReference teac = FirebaseDatabase.getInstance().getReference().child("classes").child(classId).child("members").child("teacher").getRef();
                    teac.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()){
                                for (DataSnapshot teacherSnapshot : snapshot.getChildren()){
                                    String teacherId = teacherSnapshot.getKey();
                                    if (userId.equals(teacherId)){
                                        holder.checkBox.setVisibility(View.VISIBLE);
                                        holder.checkBox.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                if (holder.checkBox.isChecked()){
                                                    arrayList_0.add(studentIds.get(position));
                                                }else {
                                                    arrayList_0.remove(studentIds.get(position));
                                                }
                                                c_box_listener.onCBoxChange(arrayList_0);
                                            }
                                        });
                                    }else {
                                        holder.checkBox.setVisibility(View.GONE);
                                    }
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled
            }
        });

        // Display attendance percentage
        if (attendancePercentages.containsKey(studentId)) {
            double percentage = attendancePercentages.get(studentId);
            holder.attendancePercentageTextView.setText(String.format(Locale.getDefault(), "%.2f%%", percentage));
        }


        holder.profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (presentCounts.containsKey(studentId) && absentCounts.containsKey(studentId)) {
                    int presentCount = presentCounts.get(studentId);
                    int absentCount = absentCounts.get(studentId);

                    Intent intent = new Intent(view.getContext(), ViewStudentActivity.class);
                    intent.putExtra("presentCount", presentCount);
                    intent.putExtra("absentCount", absentCount);
                    intent.putExtra("studentId", studentId);
                    view.getContext().startActivity(intent);
                }
            }
        });

    }


    @Override
    public int getItemCount() {
        return studentIds.size();
    }
    public List<String> getSelectedStudents() {
        return arrayList_0;
    }

    static class StudentViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profileImageView;
        private TextView usernameTextView,attendancePercentageTextView;
        private CheckBox checkBox;



        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImageView = itemView.findViewById(R.id.student_profile_item);
            usernameTextView = itemView.findViewById(R.id.student_username_item);
            checkBox = itemView.findViewById(R.id.cBox);
            attendancePercentageTextView = itemView.findViewById(R.id.attendancePercentageTextView);



        }


    }
}
