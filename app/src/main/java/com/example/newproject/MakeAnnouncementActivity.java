package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class MakeAnnouncementActivity extends AppCompatActivity {

    private static final int FILE_REQUEST_CODE = 101;

    private EditText editTextAnnouncement;
    private TextView textViewAttachment;
    private Uri fileUri;

    private FirebaseStorage firebaseStorage;
    private DatabaseReference databaseReference;

    String  classId;

    String userId;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_announcement);
        
          classId = getIntent().getStringExtra("classId");
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        userId = currentUser.getUid();



        editTextAnnouncement = findViewById(R.id.editTextAnnouncement);
        textViewAttachment = findViewById(R.id.textViewAttachment);

        firebaseStorage = FirebaseStorage.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        textViewAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });

        Button buttonPost = findViewById(R.id.button);
        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                postAnnouncement();

            }
        });
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        startActivityForResult(intent, FILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getData();
            String fileName = getFileNameFromUri(fileUri);
            textViewAttachment.setText(fileName);
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void postAnnouncement() {
        dialog=new ProgressDialog(MakeAnnouncementActivity.this);
        dialog.setMessage("Loading");
        dialog.show();
        String announcementText = editTextAnnouncement.getText().toString().trim();

            // Generate a unique announcement ID
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
            String announcementId = sdf.format(new Date());




            // Upload the file to Firebase Storage
            if (fileUri != null) {
                StorageReference storageReference = firebaseStorage.getReference("announcements")
                        .child(classId) // Replace with your class ID
                        .child(announcementId)
                        .child(userId).child(getFileNameFromUri(fileUri)); // Replace with the user ID, you may use Firebase Auth to get the user ID

                storageReference.putFile(fileUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get the download URL of the uploaded file
                                storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri downloadUri) {
                                        // Save the announcement details along with the download URL to Firebase Realtime Database
                                        saveAnnouncementToDatabase(announcementId, announcementText, downloadUri.toString());
                                        dialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MakeAnnouncementActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MakeAnnouncementActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
            } else {
                // Save the announcement details without the file download URL to Firebase Realtime Database
                saveAnnouncementToDatabase(announcementId, announcementText, null);
            }
    }

    private void saveAnnouncementToDatabase(String announcementId, String announcementText, String downloadUrl) {

        // Save the announcement details in Firebase Realtime Database
        DatabaseReference announcementsRef = databaseReference.child("classes")
                .child(classId) // Replace with your class ID
                .child("announcements")
                .child(announcementId).child(userId);
        
        if (downloadUrl != null && ! announcementText.isEmpty()){
            Announcement announcement = new Announcement(announcementText,downloadUrl,getFileNameFromUri(fileUri),userId);
            announcementsRef.setValue(announcement);
        } else if (! announcementText.isEmpty() && downloadUrl == null) {
            Announcement announcement1 = new Announcement(announcementText,null,null,userId);
            announcementsRef.setValue(announcement1);
            
        } else if (announcementText.isEmpty() && downloadUrl != null) {
            Announcement announcement2 = new Announcement(null,downloadUrl,getFileNameFromUri(fileUri),userId);
            announcementsRef.setValue(announcement2);
        }

        Toast.makeText(this, "Announcement posted successfully", Toast.LENGTH_SHORT).show();
        finish();
    }
    // Dismiss dialog before finishing the activity
    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        super.onDestroy();
    }
    // ... (other methods)
}