package com.example.examfriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.examfriend.adapters.QuestionsAdapter;
import com.example.examfriend.pojos.Exam;
import com.example.examfriend.pojos.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ExamDetailsActivity extends AppCompatActivity {

    private static final String TAG = "ExamDetailsActivity";

    private Exam exam;
    private RecyclerView rvQuestions;

//    private ArrayList<Question> questions = new ArrayList<>();
    private HashMap<String, Question> questionHashMap = new HashMap<String, Question>();

//    firebase
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Questions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_details);

//        get exam object data from previous activity
        exam = getIntent().getParcelableExtra("examObject");

//        set title as exam title & enable back button on action bar
        getSupportActionBar().setTitle(exam.getExamTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        config rv
        rvQuestions = findViewById(R.id.rv_questions);
        rvQuestions.setLayoutManager(new LinearLayoutManager(this));

//        adding items to array list of questions

        saveOrRetreiveQuestions();

    }

    private void saveOrRetreiveQuestions() {
        rootRef.child(exam.getExamUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
//                    data is already present in database, fetch it and display in rv
                    questionHashMap.clear();
                    int i = 0;
                    for (DataSnapshot snap: snapshot.getChildren()){
                        questionHashMap.put(String.valueOf(i++), snap.getValue(Question.class));
                    }
                    Log.d(TAG, "got from DB!");
                } else {
//                    data is not present in db, create dummy and save it
                    for (int i=0; i<Integer.parseInt(exam.getExamQueCount());i++){
                        Question question =
                                new Question("Q."+ String.valueOf(i+1),
                                        "(question title)", null, null,
                                        "Question Author", "Answer Author", "queImgAuthor");
                        questionHashMap.put(String.valueOf(i), question);
                    }

                    rootRef.child(exam.getExamUid())
                            .setValue(questionHashMap)
                            .addOnCompleteListener(task->{
                                Toast.makeText(getApplicationContext(), task.isSuccessful() ? "Success" : "Failure", Toast.LENGTH_SHORT).show();
                            });
                }
                rvQuestions.setAdapter(new QuestionsAdapter(getApplicationContext(), questionHashMap, exam.getExamUid()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+ error.getMessage());
            }
        });
    }
}