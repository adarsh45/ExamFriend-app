package com.example.examfriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.examfriend.adapters.ExamAdapter;
import com.example.examfriend.pojos.Exam;
import com.example.examfriend.pojos.StudentData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RecyclerView rvOngoingExams;

    private ArrayList<Exam> exams = new ArrayList<>();
    private StudentData userData;
    
    private DatabaseReference examsRef = FirebaseDatabase.getInstance().getReference("Exams");
    private DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        getUser();
        getExamsData();
    }

    private void getUser() {
        usersRef.child(currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            userData = snapshot.getValue(StudentData.class);
                        } else {
                            Toast.makeText(MainActivity.this, "User not found!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.d(TAG, "onCancelled: "+ error.getMessage());
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getExamsData() {
        examsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    exams.clear();
                    for (DataSnapshot snap: snapshot.getChildren()){
                        Exam exam = snap.getValue(Exam.class);
                        exams.add(exam);
                    }
                    rvOngoingExams.setAdapter(new ExamAdapter(getApplicationContext(), exams));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "onCancelled: "+error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        rvOngoingExams = findViewById(R.id.rv_ongoing_exams);
//        fabNewExam = findViewById(R.id.fab_new_exam);
        rvOngoingExams.setLayoutManager(new LinearLayoutManager(this));
    }

    public void addNewExam(View v){
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_new_exam);

        EditText etExamTitle = dialog.findViewById(R.id.et_exam_title);
        EditText etExamQueCount = dialog.findViewById(R.id.et_exam_questions_count);
        Button btnCancelExam = dialog.findViewById(R.id.btn_cancel_exam);
        Button btnCreateExam = dialog.findViewById(R.id.btn_create_exam);

        btnCreateExam.setOnClickListener(view-> {
            createNewExam(etExamTitle, etExamQueCount);
            dialog.dismiss();
        });
        btnCancelExam.setOnClickListener(view->{
            dialog.dismiss();
        });
        dialog.show();
    }

    private void createNewExam(EditText etExamTitle, EditText etExamQueCount){
        String examTitle = etExamTitle.getText().toString();
        String examQueCount = etExamQueCount.getText().toString();
        if (TextUtils.isEmpty(examTitle)){
            etExamTitle.setError("Exam Title cannot be empty!");
            return;
        }
        if (TextUtils.isEmpty(examQueCount)){
            etExamQueCount.setError("Qustion count cannot be empty!");
            return;
        }
        if (userData == null){
            Toast.makeText(this, "User not found, Please retry!", Toast.LENGTH_SHORT).show();
            mAuth.signOut();
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();
            return;
        }
        String examUid = examsRef.push().getKey();
        Exam exam = new Exam(examUid, examTitle, examQueCount, userData.getName(), userData.getUid());
        examsRef.child(examUid != null ? examUid : "null")
                .setValue(exam).addOnCompleteListener(task->{
            Toast.makeText(this, task.isSuccessful() ? "Created "+ examTitle + " exam successfully!" : "Something went wrong!", Toast.LENGTH_SHORT).show();
        });
    }
}