package com.example.newproject;

// ClassAdapter.java

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Random;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ClassViewHolder> {

    private List<ClassInfo> classList;
    private int[] backgroundImages;

    public ClassAdapter(List<ClassInfo> classList, int[] backgroundImages) {
        this.classList = classList;
        this.backgroundImages = backgroundImages;
    }

    @NonNull
    @Override
    public ClassViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ClassViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassViewHolder holder, int position) {
        ClassInfo currentClass = classList.get(position);
        String classId = currentClass.getClassId();
        holder.textClassName.setText(currentClass.getClassName());
        holder.textSectionSubjectRoom.setText(currentClass.getSection() + " | " + currentClass.getSubject() + " | " + currentClass.getRoom());
        holder.etClassId.setText(currentClass.getClassId());
        // Set the background image for the item randomly
        int randomImage = backgroundImages[new Random().nextInt(backgroundImages.length)];
        holder.itemView.setBackgroundResource(randomImage);

        holder.classLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(),ViewClassActivity.class);
                intent.putExtra("classId", classId);
               view.getContext(). startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return classList.size();
    }

    public static class ClassViewHolder extends RecyclerView.ViewHolder {
        TextView textClassName;
        TextView textSectionSubjectRoom;
        EditText etClassId;
        private LinearLayout classLinearLayout;

        public ClassViewHolder(View itemView) {
            super(itemView);
            textClassName = itemView.findViewById(R.id.textClassName);
            textSectionSubjectRoom = itemView.findViewById(R.id.textSectionSubjectRoom);
            classLinearLayout = itemView.findViewById(R.id.itemClassBack);
            etClassId = itemView.findViewById(R.id.class_id_item);
        }
    }
}
