package tk.therealsuji.vtopchennai.adapters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.color.MaterialColors;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import tk.therealsuji.vtopchennai.R;
import tk.therealsuji.vtopchennai.fragments.AssignmentViewFragment;
import tk.therealsuji.vtopchennai.models.Assignment;

public class AssignmentsItemAdapter extends RecyclerView.Adapter<AssignmentsItemAdapter.ViewHolder> {
    List<Assignment> assignments;



    public AssignmentsItemAdapter(List<Assignment> assignments) {
        this.assignments = assignments;


        // Create the first assignment
        Assignment englishAssignment = new Assignment();
        englishAssignment.course = "English";
        englishAssignment.title = "Write an essay on IPL";
        englishAssignment.dueDate = new Date().getTime(); // Add a fake due date
        assignments.add(englishAssignment);

        // Create the second assignment
        Assignment mathsAssignment = new Assignment();
        mathsAssignment.course = "Maths";
        mathsAssignment.title = "Complete exercise 9.2";
        mathsAssignment.dueDate = new Date().getTime(); // Add a fake due date
        assignments.add(mathsAssignment);

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout assignmentItem = (LinearLayout) LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_item_assignments, parent, false);

        return new ViewHolder(assignmentItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setAssignment(this.assignments.get(position));
    }

    @Override
    public int getItemCount() {
        return this.assignments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout assignmentsItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.assignmentsItem = (LinearLayout) itemView;
        }

        public void setAssignment(Assignment assignmentsItem) {
            TextView title = this.assignmentsItem.findViewById(R.id.text_view_title);
            TextView course = this.assignmentsItem.findViewById(R.id.text_view_course);

            title.setText(assignmentsItem.title);
            course.setText(assignmentsItem.course);

            if (assignmentsItem.dueDate != null && assignmentsItem.dueDate < Calendar.getInstance().getTime().getTime()) {
                ImageView endDrawable = this.assignmentsItem.findViewById(R.id.image_view_past_due);
                endDrawable.setImageDrawable(ContextCompat.getDrawable(this.assignmentsItem.getContext(), R.drawable.ic_clock));
                DrawableCompat.setTint(
                        DrawableCompat.wrap(endDrawable.getDrawable()),
                        MaterialColors.getColor(endDrawable, R.attr.colorError)
                );
            }

            this.assignmentsItem.setOnClickListener(view -> {
                // Display the assignment data when the item is clicked
                Toast.makeText(view.getContext(), "Title: " + assignmentsItem.title + "\nCourse: " + assignmentsItem.course + "\nDue Date: " + new Date(assignmentsItem.dueDate), Toast.LENGTH_LONG).show();
            });
        }
    }
}