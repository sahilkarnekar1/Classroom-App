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
import com.squareup.picasso.Picasso;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.AnnouncementViewHolder> {

    private List<Announcement> announcementList;
    private Context context;

    public AnnouncementAdapter(List<Announcement> announcementList, Context context) {
        this.announcementList = announcementList;
        this.context = context;
    }

    @NonNull
    @Override
    public AnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement, parent, false);
        return new AnnouncementViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementViewHolder holder, int position) {
        Announcement announcement = announcementList.get(position);

        holder.textAnnouncement.setText(announcement.getAnnouncementText());

        // Check if there is a file URL, and if so, show the download option.
        if (announcement.getFileUrl() != null) {
            holder.textFileName.setText(announcement.getFileName());
            holder.textFileName.setVisibility(View.VISIBLE);
            holder.textDownload.setVisibility(View.VISIBLE);
            holder.textDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Handle the download file action here.
                    // You may use the announcement.getFileUrl() to download the file.
                    openFile(announcement.getFileUrl(), v.getContext());
                }
            });
        } else {
            holder.textDownload.setVisibility(View.GONE);
            holder.textFileName.setVisibility(View.GONE);
        }
        fetchAndDisplayUsername(announcement.getUserId(), holder.textUserNameAnnouncement,holder.profileImageAnnouncement);
    }

    private void fetchAndDisplayUsername(String userId, TextView textView, ImageView circleImageView) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Users user = dataSnapshot.getValue(Users.class);
                    if (user != null) {
                        String username = user.getUserName();
                        textView.setText("Posted by: " + username);

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
        usersRef.child("profile").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String profileIV = snapshot.getValue().toString();
                    Picasso.get().load(profileIV).into(circleImageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

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
        return announcementList.size();
    }

    public static class AnnouncementViewHolder extends RecyclerView.ViewHolder {
        public TextView textAnnouncement;
        public ImageView textDownload;
        public TextView textFileName;
        public ImageView profileImageAnnouncement;
        public TextView textUserNameAnnouncement;

        public AnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            textAnnouncement = itemView.findViewById(R.id.textItemAnnouncement);
            textDownload = itemView.findViewById(R.id.textItemDownload);
            textFileName = itemView.findViewById(R.id.textItemAnnouncementFileName);
            textUserNameAnnouncement = itemView.findViewById(R.id.textUserNameAnnouncement);
            profileImageAnnouncement=itemView.findViewById(R.id.profile_image_announcement);
        }
    }
}
