package com.example.examfriend.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examfriend.ExamDetailsActivity;
import com.example.examfriend.R;
import com.example.examfriend.pojos.Exam;

import java.util.ArrayList;

public class ExamAdapter extends RecyclerView.Adapter<ExamAdapter.ExamViewHolder> {

    private ArrayList<Exam> exams;
    private Context context;

    public ExamAdapter(Context context, ArrayList<Exam> exams){
        this.context = context;
        this.exams = exams;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_exam,parent,false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        Exam examObj = exams.get(position);
        holder.tvExamTitle.setText(examObj.getExamTitle());
        holder.tvExamAuthorName.setText(examObj.getAuthorName());

        holder.itemView.setOnClickListener(view->{
            Intent intent = new Intent(context, ExamDetailsActivity.class);
            intent.putExtra("examObject", examObj);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return exams.size();
    }

    public class ExamViewHolder extends RecyclerView.ViewHolder{

        public TextView tvExamTitle, tvExamAuthorName;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            tvExamTitle = itemView.findViewById(R.id.tv_exam_title);
            tvExamAuthorName = itemView.findViewById(R.id.tv_exam_author_name);
        }
    }
}
