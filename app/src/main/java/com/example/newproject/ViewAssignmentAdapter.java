package com.example.newproject;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class ViewAssignmentAdapter extends RecyclerView.Adapter<ViewAssignmentAdapter.ViewAssignmentViewHolder>{

    private List<StudentAssignment> assignmentsList;
    private Context context;

    public ViewAssignmentAdapter(List<StudentAssignment> assignmentsList, Context context) {
        this.assignmentsList = assignmentsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewAssignmentAdapter.ViewAssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.submitted_item_layout, parent, false);
        return new ViewAssignmentAdapter.ViewAssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewAssignmentAdapter.ViewAssignmentViewHolder holder, int position) {
StudentAssignment studentAssignment =assignmentsList.get(position);
String userId = studentAssignment.getUserId();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user = snapshot.getValue(Users.class);
                String userName = user.getUserName();
                holder.usernameTV.setText(userName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (studentAssignment.getDownloadUrl() != null) {
            holder.filenameTV.setText(studentAssignment.getFileName());
            holder.filenameTV.setVisibility(View.VISIBLE);
            holder.downloadBtn.setVisibility(View.VISIBLE);
            holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the download file action here.
                    // You may use the announcement.getFileUrl() to download the file.
                    openFile(studentAssignment.getDownloadUrl(), v.getContext());
                }
            });
        } else {
            holder.downloadBtn.setVisibility(View.GONE);
            holder.filenameTV.setVisibility(View.GONE);
        }
    }
    private void openFile(String fileUrl, Context context) {
        Uri uri = Uri.parse(fileUrl);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getMimeType(uri)); // Set the appropriate MIME type

        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // Handle the case where no compatible app is available to open the file
            Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show();
        }
    }

    private String getMimeType(Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.getType(uri);
    }

    @Override
    public int getItemCount() {
        return assignmentsList.size();
    }

    public class ViewAssignmentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameTV,filenameTV;
        ImageView downloadBtn;
        public ViewAssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameTV=itemView.findViewById(R.id.submittedUsername);
            filenameTV=itemView.findViewById(R.id.submittedFilename);
            downloadBtn=itemView.findViewById(R.id.submittedDownloadBtn);
        }
    }
}
