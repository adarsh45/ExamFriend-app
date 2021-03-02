package com.example.examfriend.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.examfriend.QuestionDetailsActivity;
import com.example.examfriend.R;
import com.example.examfriend.pojos.Question;

import java.util.ArrayList;
import java.util.HashMap;

public class QuestionsAdapter  extends RecyclerView.Adapter<QuestionsAdapter.QuestionsViewHolder> {

    private Context context;
    private HashMap<String, Question> questionsMap;
    private String examUid;

    public QuestionsAdapter(Context context, HashMap<String, Question> questionsMap, String examUid){
        this.context = context;
        this.questionsMap = questionsMap;
        this.examUid = examUid;
    }

    @NonNull
    @Override
    public QuestionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_question, parent, false);
        return new QuestionsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionsViewHolder holder, int position) {
//        get object from position as key
        Question question = questionsMap.get(String.valueOf(position));
//        set texts & img to rv view holder item
        holder.tvQuestionCount.setText(question.getQueCount());
        holder.tvQuestionTitle.setText(question.getQueTitle());
        if (!TextUtils.isEmpty(question.getQueAnswer())){
            holder.imgAnswerStatus.setImageResource(R.drawable.ic_baseline_check_circle_24);
        }

//        on click listener for view holder
        holder.itemView.setOnClickListener(view->{
            Toast.makeText(context, "Hello", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(context, QuestionDetailsActivity.class);
            intent.putExtra("questionPosition", position);
            intent.putExtra("questionObject", question);
            intent.putExtra("examUid", examUid);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return questionsMap.size();
    }

    public class QuestionsViewHolder extends RecyclerView.ViewHolder{

        public TextView tvQuestionCount, tvQuestionTitle;
        public ImageView imgAnswerStatus;
        public QuestionsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionCount = itemView.findViewById(R.id.tv_question_count);
            tvQuestionTitle = itemView.findViewById(R.id.tv_question_title);
            imgAnswerStatus = itemView.findViewById(R.id.img_answer_status);
        }
    }
}
