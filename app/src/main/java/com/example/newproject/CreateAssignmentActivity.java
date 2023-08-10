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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateAssignmentActivity extends AppCompatActivity {
 private EditText etTitle,etDescription;
 private Button btnAssignment;
 private TextView txtAttachment;
 int FILE_REQUEST_CODE = 11;
    private Uri fileUri;
    ProgressDialog dialog;
    String classId,userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_assignment);

        etTitle=findViewById(R.id.editTextTitleAttachment);
        etDescription=findViewById(R.id.editTextDescAttachment);
        btnAssignment=findViewById(R.id.post_Assignment);
        txtAttachment=findViewById(R.id.Assignment_attachment);

        classId = getIntent().getStringExtra("classId");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        userId = currentUser.getUid();

        txtAttachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });
        btnAssignment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            txtAttachment.setText(fileName);
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
        dialog=new ProgressDialog(CreateAssignmentActivity.this);
        dialog.setMessage("Loading");
        dialog.show();
        String Title = etTitle.getText().toString().trim();
        String Description = etDescription.getText().toString().trim();

        // Generate a unique announcement ID
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String announcementId = sdf.format(new Date());




        // Upload the file to Firebase Storage
        if (fileUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("Assignments")
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
                                    saveAnnouncementToDatabase(announcementId, Title,Description, downloadUri.toString());
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CreateAssignmentActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(CreateAssignmentActivity.this, "Error"+e, Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
        } else {
            // Save the announcement details without the file download URL to Firebase Realtime Database
            saveAnnouncementToDatabase(announcementId, Title, Description,null);
        }
    }

    private void saveAnnouncementToDatabase(String announcementId, String announcementText,String Description, String downloadUrl) {

        // Save the announcement details in Firebase Realtime Database
        DatabaseReference announcementsRef = FirebaseDatabase.getInstance().getReference().child("classes")
                .child(classId) // Replace with your class ID
                .child("assignments")
                .child(announcementId).child(userId);

       if (downloadUrl != null && !announcementText.isEmpty() && !Description.isEmpty()){
           Assignments assignments1 = new Assignments(announcementText,Description,downloadUrl,getFileNameFromUri(fileUri),userId,announcementId,classId);
           announcementsRef.setValue(assignments1);
       } else if (downloadUrl != null && !announcementText.isEmpty() && Description.isEmpty()) {
           Assignments assignments2 = new Assignments(announcementText,null,downloadUrl,getFileNameFromUri(fileUri),userId,announcementId,classId);
           announcementsRef.setValue(assignments2);
       }else if (downloadUrl != null && announcementText.isEmpty() && !Description.isEmpty()) {
           Assignments assignments3 = new Assignments(null,Description,downloadUrl,getFileNameFromUri(fileUri),userId,announcementId,classId);
           announcementsRef.setValue(assignments3);
       }else if (downloadUrl != null && announcementText.isEmpty() && Description.isEmpty()) {
           Assignments assignments4 = new Assignments(null,null,downloadUrl,getFileNameFromUri(fileUri),userId,announcementId,classId);
           announcementsRef.setValue(assignments4);
       }else if (downloadUrl == null && !announcementText.isEmpty() && !Description.isEmpty()) {
           Assignments assignments5 = new Assignments(announcementText,Description,null,null,userId,announcementId,classId);
           announcementsRef.setValue(assignments5);
       }else if (downloadUrl == null && !announcementText.isEmpty() && Description.isEmpty()) {
           Assignments assignments6 = new Assignments(announcementText,null,null,null,userId,announcementId,classId);
           announcementsRef.setValue(assignments6);
       }else if (downloadUrl == null && announcementText.isEmpty() && !Description.isEmpty()) {
           Assignments assignments7 = new Assignments(null,Description,null,null,userId,announcementId,classId);
           announcementsRef.setValue(assignments7);
       }else if (downloadUrl == null && announcementText.isEmpty() && Description.isEmpty()) {
           Toast.makeText(this, "Please Fill Fields", Toast.LENGTH_SHORT).show();
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
}