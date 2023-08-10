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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.AssignmentViewHolder>{
    private List<Assignments> assignmentsList;
    private Context context;

    public AssignmentAdapter(List<Assignments> assignmentsList, Context context) {
        this.assignmentsList = assignmentsList;
        this.context = context;
    }

    @NonNull
    @Override
    public AssignmentAdapter.AssignmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.assignment_item, parent, false);
        return new AssignmentAdapter.AssignmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssignmentAdapter.AssignmentViewHolder holder, int position) {
        Assignments announcement = assignmentsList.get(position);
        String assignmentId = announcement.getAssignmentId();
        String classId = announcement.getClassId();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId= firebaseUser.getUid();
        holder.txtTitle.setText(announcement.getAssignmentTitle());
        holder.txtDesc.setText(announcement.getAssignmentDesc());

        if (announcement.getFileUri() != null) {
            holder.txtFilename.setText(announcement.getFileUri());
            holder.txtFilename.setVisibility(View.VISIBLE);
            holder.btnDownload.setVisibility(View.VISIBLE);
            holder.btnDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the download file action here.
                    // You may use the announcement.getFileUrl() to download the file.
                    openFile(announcement.getDownloadUrl(), v.getContext());
                }
            });
        } else {
            holder.btnDownload.setVisibility(View.GONE);
            holder.txtFilename.setVisibility(View.GONE);
        }
        holder.txtAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),SubmitWorkActivity.class);
                intent.putExtra("assignmentId", assignmentId);
                intent.putExtra("classId", classId);
                view.getContext(). startActivity(intent);
            }
        });

        if (announcement.getUserId() != null && announcement.getUserId().equals(userId)){
            holder.viewSubmitedAssignments.setVisibility(View.VISIBLE);
            holder.viewSubmitedAssignments.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(),SubmittedStudentsActivity.class);
                    intent.putExtra("assignmentId", assignmentId);
                    intent.putExtra("classId", classId);
                    view.getContext(). startActivity(intent);
                }
            });
        }else {
            holder.viewSubmitedAssignments.setVisibility(View.GONE);
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

    public class AssignmentViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle,txtDesc,txtFilename,txtAttachment;
        private ImageView btnDownload;
        ImageView viewSubmitedAssignments;
        public AssignmentViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle=itemView.findViewById(R.id.textViewTitleAss);
            txtDesc=itemView.findViewById(R.id.textViewDescriptionAss);
            txtFilename=itemView.findViewById(R.id.textViewFileNameAss);
            btnDownload=itemView.findViewById(R.id.buttonDownloadAss);
            txtAttachment=itemView.findViewById(R.id.selectAttachmentAssignmentItem);
            viewSubmitedAssignments=itemView.findViewById(R.id.viewSubmitedAssignmentsIV);
        }
    }
}
