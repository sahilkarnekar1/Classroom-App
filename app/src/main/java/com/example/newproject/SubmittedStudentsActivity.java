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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SubmittedStudentsActivity extends AppCompatActivity {
    String classId,assignmentId;
    RecyclerView studentRecView;
    private ViewAssignmentAdapter assignmentAdapter;
    private List<StudentAssignment> assignmentsList;
    private DatabaseReference assignmentRef;
    ImageView btnExport;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submitted_students);
        studentRecView=findViewById(R.id.submittedRecView);
        btnExport=findViewById(R.id.exportButton);

        classId = getIntent().getStringExtra("classId");
        assignmentId = getIntent().getStringExtra("assignmentId");

        assignmentRef = FirebaseDatabase.getInstance().getReference().child("classes")
                .child(classId) // Replace with your class ID
                .child("assignments").child(assignmentId);

//btnExport.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//
//
//        assignmentRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                DataSnapshot firstChildSnapshot = dataSnapshot.getChildren().iterator().next();
//                DatabaseReference assref = firstChildSnapshot.child("StudentsAssignments").getRef();
//
//                assref.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        Workbook wb = new HSSFWorkbook();
//                        Sheet sheet = wb.createSheet("sahil1241");
//
//                        final int[] rowIndex = {0}; // Create an array to hold the index
//
//                        for (DataSnapshot announcementSnapshot : dataSnapshot.getChildren()) {
//                            StudentAssignment announcement = announcementSnapshot.getValue(StudentAssignment.class);
//                            String userId = announcement.getUserId();
//
//                            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
//                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                    Users user = snapshot.getValue(Users.class);
//                                    String userName = user.getUserName();
//
//                                    // Inside the onDataChange callback, create the row and set values
//                                    Row row = sheet.createRow(rowIndex[0]++);
//                                    Cell cell = row.createCell(0);
//                                    cell.setCellValue(userName);
//                                    cell = row.createCell(1);
//                                    cell.setCellValue(announcement.getFileName());
//
//                                    // Check if this is the last row and then save the Excel file
//                                    if (rowIndex[0] == dataSnapshot.getChildrenCount()) {
//                                        sheet.setColumnWidth(0, (10 * 200));
//                                        sheet.setColumnWidth(1, (10 * 200));
//
//                                        File file = new File(getExternalFilesDir(null), "p1.xls");
//                                        FileOutputStream outputStream = null;
//                                        try {
//                                            outputStream = new FileOutputStream(file);
//                                            wb.write(outputStream);
//                                            Toast.makeText(SubmittedStudentsActivity.this, "ok", Toast.LENGTH_SHORT).show();
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        } finally {
//                                            if (outputStream != null) {
//                                                try {
//                                                    outputStream.close();
//                                                } catch (IOException e) {
//                                                    e.printStackTrace();
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    // Handle onCancelled if needed
//                                }
//                            });
//                        }
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        // Handle error if needed
//                    }
//                });
//
//            }
//        });
//
//
//    }
//});




        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        studentRecView.setLayoutManager(linearLayoutManager);


        assignmentsList = new ArrayList<>();
        assignmentAdapter = new ViewAssignmentAdapter(assignmentsList, this);

        // Use custom divider drawable (divider.xml)
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, LinearLayoutManager.VERTICAL);
        dividerItemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider));
        studentRecView.addItemDecoration(dividerItemDecoration);

        studentRecView.setAdapter(assignmentAdapter);



        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                assignmentRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        DataSnapshot firstChildSnapshot = dataSnapshot.getChildren().iterator().next();
                        DatabaseReference assref = firstChildSnapshot.child("StudentsAssignments").getRef();

                        assref.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
                                String sName = sdf.format(new Date());
                                Workbook wb = new HSSFWorkbook();
                                Sheet sheet = wb.createSheet(sName);

                                final int[] rowIndex = {0}; // Create an array to hold the index

                                for (DataSnapshot announcementSnapshot : dataSnapshot.getChildren()) {
                                    StudentAssignment announcement = announcementSnapshot.getValue(StudentAssignment.class);
                                    String userId = announcement.getUserId();

                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(userId);
                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            Users user = snapshot.getValue(Users.class);
                                            String userName = user.getUserName();

                                            // Inside the onDataChange callback, create the row and set values
                                            Row row = sheet.createRow(rowIndex[0]++);
                                            Cell cell = row.createCell(0);
                                            cell.setCellValue(userName);
                                            cell = row.createCell(1);
                                            cell.setCellValue(announcement.getFileName());

                                            // Check if this is the last row and then save the Excel file
                                            if (rowIndex[0] == dataSnapshot.getChildrenCount()) {
                                                sheet.setColumnWidth(0, (10 * 200));
                                                sheet.setColumnWidth(1, (10 * 200));

                                                File file = new File(getExternalFilesDir(null),sName+".xls");
                                                FileOutputStream outputStream = null;
                                                try {
                                                    outputStream = new FileOutputStream(file);
                                                    wb.write(outputStream);
                                                    Toast.makeText(SubmittedStudentsActivity.this, "File created at Android/data/com.example.newproject/files/"+sName+".xls", Toast.LENGTH_LONG).show();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                } finally {
                                                    if (outputStream != null) {
                                                        try {
                                                            outputStream.close();
                                                        } catch (IOException e) {
                                                            e.printStackTrace();
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle onCancelled if needed
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // Handle error if needed
                            }
                        });
                    }
                });
            }
        });

    }
    private void loadAnnouncements() {

        assignmentRef.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                DataSnapshot firstChildSnapshot = dataSnapshot.getChildren().iterator().next();
                DatabaseReference assref = firstChildSnapshot.child("StudentsAssignments").getRef();

                assref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        assignmentsList.clear();
                        for (DataSnapshot announcementSnapshot : dataSnapshot.getChildren()) {

                                StudentAssignment announcement = announcementSnapshot.getValue(StudentAssignment.class);
                                assignmentsList.add(announcement);
                        }
                        assignmentAdapter.notifyDataSetChanged();
                        // Scroll the RecyclerView to the bottom
                        if (!assignmentsList.isEmpty()) {
                            studentRecView.scrollToPosition(assignmentsList.size() - 1);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle error if needed
                    }
                });

            }
        });





    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAnnouncements();

    }

}