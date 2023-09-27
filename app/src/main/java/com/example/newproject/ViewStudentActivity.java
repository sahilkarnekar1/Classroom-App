package com.example.newproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;

public class ViewStudentActivity extends AppCompatActivity {

    String studentId;
    int presentCount,absentCount,totalDays;
    double attendancePercentage;
    TextView studentUsername,studentAttendancepercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student);

        studentAttendancepercent = findViewById(R.id.stdTVAttendancepercent);
        studentUsername = findViewById(R.id.stdTV);



        studentId = getIntent().getStringExtra("studentId");
        presentCount = getIntent().getIntExtra("presentCount", 0); // Provide a default value if needed
        absentCount = getIntent().getIntExtra("absentCount", 0); // Provide a default value if needed

        totalDays = presentCount+absentCount;
      attendancePercentage = (double) presentCount / totalDays * 100;

        // Create a BarChart and set its data
        BarChart barChart = findViewById(R.id.barChart);
        ArrayList<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, (float) presentCount));
        entries.add(new BarEntry(1, (float) absentCount));

        BarDataSet dataSet = new BarDataSet(entries, "Attendance");
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        ArrayList<String> labels = new ArrayList<>();
        labels.add("Present");
        labels.add("Absent");

        BarData data = new BarData(dataSet);
        barChart.setData(data);

        barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(labels));

        // Customize the chart
        data.setBarWidth(0.5f);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(2000);

        // Show the chart
        barChart.invalidate();


        DatabaseReference studentRef = FirebaseDatabase.getInstance().getReference().child("users").child(studentId).child("userName");
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String username = snapshot.getValue(String.class);
                    studentUsername.setText(username);
                } else {
                    // Handle the case where the profile image data doesn't exist
                    // For example, you can set a default image here
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        studentAttendancepercent.setText(String.format(Locale.getDefault(), "%.2f%%", attendancePercentage));
    }
}