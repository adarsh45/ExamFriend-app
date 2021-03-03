package com.example.examfriend;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.examfriend.Utils.Utils;
import com.example.examfriend.pojos.Question;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;

public class QuestionDetailsActivity extends AppCompatActivity {
    
    private static final String TAG = "QuestionDetailsActivity";
    private static final int REQUEST_CAMERA = 11;
    private static final int REQUEST_GALLERY = 12;

    private Question questionObject;
    private int questionPosition;
    private String examUid;

//    views
    private ConstraintLayout layout;
    private InputMethodManager imm;
    private TextView tvQuestionTitle, tvAnswerText;
    private EditText etQuestion, etAnswer;
    private ImageButton btnEditAnswer, btnChangeAnswer, btnCancelChangeAnswer;
    private ImageButton btnEditQuestion, btnChangeQuestion, btnCancelChangeQuestion;
    private PhotoView imgQuestionPhoto;
    private Button btnUploadPhoto;

//    firebase
    private DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference("Questions");
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference("Exams");
    private StorageReference photoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_details);

        initViews();

        if (getIntent().getExtras() != null){
            questionPosition = getIntent().getIntExtra("questionPosition",-1);
            questionObject = getIntent().getParcelableExtra("questionObject");
            examUid = getIntent().getStringExtra("examUid");
        }

        if (questionPosition != -1){
            getSupportActionBar().setTitle("Q."+ String.valueOf(questionPosition+1));
        }
        setTexts();
    }

    private void setTexts() {
        if (questionObject == null){
            Toast.makeText(this, "Question could not be loaded! Try again!", Toast.LENGTH_SHORT).show();
            return;
        }
        tvQuestionTitle.setText(questionObject.getQueTitle());
        etQuestion.setText(questionObject.getQueTitle());
        tvAnswerText.setText(questionObject.getQueAnswer());
        etAnswer.setText(questionObject.getQueAnswer());

        if (!TextUtils.isEmpty(questionObject.getQueImgUrl())){
            imgQuestionPhoto.setVisibility(View.VISIBLE);
            btnUploadPhoto.setVisibility(View.GONE);
        }

//        check if photo is present
        photoRef = storageRef.child(examUid).child(String.valueOf(questionPosition));
        photoRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
//                file is present
                    btnUploadPhoto.setVisibility(View.GONE);
                    imgQuestionPhoto.setVisibility(View.VISIBLE);
                    Glide.with(this).load(uri.toString()).into(imgQuestionPhoto);
                    Log.d(TAG, "onSuccess: File is present already!");
                })
                .addOnFailureListener(e -> {
//                file not found , proceed to upload if wanted
                    btnUploadPhoto.setVisibility(View.VISIBLE);
                    imgQuestionPhoto.setVisibility(View.GONE);
                    Log.d(TAG, "onFailure: File not found!");
                    Log.d(TAG, "onFailure: "+ e.getLocalizedMessage());
                });
    }

    private void initViews() {
        layout = findViewById(R.id.root_layout);
        tvQuestionTitle = findViewById(R.id.tv_que_title);
        tvAnswerText = findViewById(R.id.tv_answer);
        etQuestion = findViewById(R.id.et_question);
        etAnswer = findViewById(R.id.et_answer);
//        photo
        imgQuestionPhoto = findViewById(R.id.img_que_photo);
        btnUploadPhoto = findViewById(R.id.btn_upload_photo);
//        buttons
        btnEditQuestion = findViewById(R.id.btn_edit_question);
        btnChangeQuestion = findViewById(R.id.btn_change_question);
        btnCancelChangeQuestion = findViewById(R.id.btn_cancel_change_question);

        btnEditAnswer = findViewById(R.id.btn_edit_answer);
        btnChangeAnswer = findViewById(R.id.btn_change_answer);
        btnCancelChangeAnswer = findViewById(R.id.btn_cancel_change_answer);

//        input manager for hiding keyboard after use
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void selectPhoto(View view){
        CharSequence[] options = {"Take Photo", "Select from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Image");
        builder.setItems(options, (dialog, which) -> {
           switch (which){
               case 0:
//                   take photo
                   if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                           != PackageManager.PERMISSION_GRANTED){
                       Toast.makeText(this, "Please approve permission!", Toast.LENGTH_SHORT).show();
                       ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA);
                       return;
                   }
                   Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                   startActivityForResult(cameraIntent, REQUEST_CAMERA);
                   break;
               case 1:
//                   open gallery
                   if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                           != PackageManager.PERMISSION_GRANTED){
                       Toast.makeText(this, "Please approve permission!", Toast.LENGTH_SHORT).show();
                       ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_GALLERY);
                       return;
                   }
                   Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                   galleryIntent.setType("image/*");
                   startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), REQUEST_GALLERY);
                   break;
               case 2:
//                   cancel
                   Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
                   dialog.dismiss();
                   break;
           }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
            switch (requestCode){
                case REQUEST_CAMERA:
                    Toast.makeText(this, "Opening camera", Toast.LENGTH_SHORT).show();
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                    break;
                case REQUEST_GALLERY:
                    Toast.makeText(this, "Opening Gallery", Toast.LENGTH_SHORT).show();
                    Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    galleryIntent.setType("image/*");
                    startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), REQUEST_GALLERY);
                    break;
                default:
                    Toast.makeText(this, "Unknown request provided!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error permission denied!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && data != null){
            byte[] imageBytes = new byte[0];
            switch (requestCode){
                case REQUEST_CAMERA:
                    Bundle bundle = data.getExtras();
                    Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                    WeakReference<Bitmap> result1 = new WeakReference<Bitmap>(Bitmap.createScaledBitmap(thumbnail,
                            thumbnail.getWidth(), thumbnail.getHeight(), false).copy(
                            Bitmap.Config.RGB_565, true));
                    Bitmap bm=result1.get();
                    final Bitmap bitmap = (Bitmap) bundle.get("data");
//                    convert bitmap to bytes[]
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
//                    imageBytes = baos.toByteArray();
//                    another method
//                    int size = bitmap.getRowBytes() * bitmap.getHeight();
//                    ByteBuffer byteBuffer = ByteBuffer.allocate(size);
//                    bitmap.copyPixelsToBuffer(byteBuffer);
//                    imageBytes = byteBuffer.array();
//
//                    uploadPhoto(imageBytes);
//                    set image locally & visibility of imgView and button
                    imgQuestionPhoto.setImageBitmap(bm);
                    imgQuestionPhoto.setVisibility(View.VISIBLE);
                    btnUploadPhoto.setVisibility(View.GONE);
                    break;
                case REQUEST_GALLERY:
                    Uri selectedImageUri = data.getData();
//                    convert uri to bytes[]
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        imageBytes = Utils.getBytes(inputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    uploadPhoto(imageBytes);
//                    set image locally & visibility of imgView and button
                    imgQuestionPhoto.setImageURI(selectedImageUri);
                    imgQuestionPhoto.setVisibility(View.VISIBLE);
                    btnUploadPhoto.setVisibility(View.GONE);
                    break;
                default:
                    Toast.makeText(this, "Unknown request code!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Result is not ok!", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadPhoto(byte[] imageBytes){
        if (imageBytes == null){
            Log.d(TAG, "uploadPhoto: image bytes[] was empty!");
            Toast.makeText(this, "image bytes[] was empty!", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.d(TAG, "uploadPhoto: STARTED");
        photoRef = storageRef.child(examUid).child(String.valueOf(questionPosition));
        UploadTask uploadTask = photoRef.putBytes(imageBytes);
        uploadTask.addOnFailureListener(exception-> {
            Log.d(TAG, "uploadPhoto: "+ exception.getLocalizedMessage());
            Toast.makeText(this, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            Log.d(TAG, "uploadPhoto: "+ taskSnapshot.toString());
            Toast.makeText(this, "Uploaded!", Toast.LENGTH_SHORT).show();
        });
    }

    public void edit(View view){
        if (view.getId() == R.id.btn_edit_question){
            //        changing visibility of buttons
            btnEditQuestion.setVisibility(View.GONE);
            btnChangeQuestion.setVisibility(View.VISIBLE);
            btnCancelChangeQuestion.setVisibility(View.VISIBLE);

            //        changing visibility of et & tv
            tvQuestionTitle.setVisibility(View.GONE);
            etQuestion.setVisibility(View.VISIBLE);
        } else if (view.getId() == R.id.btn_edit_answer){
            //        changing visibility of buttons
            btnEditAnswer.setVisibility(View.GONE);
            btnChangeAnswer.setVisibility(View.VISIBLE);
            btnCancelChangeAnswer.setVisibility(View.VISIBLE);

            //        changing visibility of et & tv
            tvAnswerText.setVisibility(View.GONE);
            etAnswer.setVisibility(View.VISIBLE);
        }
    }

    public void change(View view){
        if (view.getId() == R.id.btn_change_question){
            String updatedQuestion = etQuestion.getText().toString();
            if (TextUtils.isEmpty(updatedQuestion)){
                etQuestion.setError("Question cannot be empty!");
                return;
            }
            if (TextUtils.isEmpty(examUid)){
                Toast.makeText(this, "Exam not found! Try again!", Toast.LENGTH_SHORT).show();
                return;
            }
            rootRef.child(examUid).child(String.valueOf(questionPosition)).child("queTitle")
                    .setValue(updatedQuestion).addOnCompleteListener(task->{
                if (task.isSuccessful()){
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    questionObject.setQueTitle(updatedQuestion);
                    tvQuestionTitle.setText(updatedQuestion);
                    etQuestion.setText(updatedQuestion);
                } else {
                    Log.d(TAG, "change: UPDATE QUESTION FAILED!");
                    Toast.makeText(this, "Failure!", Toast.LENGTH_SHORT).show();
                }
            });

            btnEditQuestion.setVisibility(View.VISIBLE);
            btnChangeQuestion.setVisibility(View.GONE);
            btnCancelChangeQuestion.setVisibility(View.GONE);
            //        changing visibility of et & tv
            tvQuestionTitle.setVisibility(View.VISIBLE);
            etQuestion.setVisibility(View.GONE);

        } else if (view.getId() == R.id.btn_change_answer){

            String updatedAnswer = etAnswer.getText().toString();
            if (TextUtils.isEmpty(updatedAnswer)){
                etAnswer.setError("Answer cannot be empty!");
                return;
            }
            if (TextUtils.isEmpty(examUid)){
                Toast.makeText(this, "Exam not found! Try again!", Toast.LENGTH_SHORT).show();
                return;
            }
            rootRef.child(examUid).child(String.valueOf(questionPosition)).child("queAnswer")
                    .setValue(updatedAnswer).addOnCompleteListener(task->{
                if (task.isSuccessful()){
                    Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
                    questionObject.setQueAnswer(updatedAnswer);
                    tvAnswerText.setText(updatedAnswer);
                    etAnswer.setText(updatedAnswer);
                } else {
                    Log.d(TAG, "change: UPDATE QUESTION FAILED!");
                    Toast.makeText(this, "Failure!", Toast.LENGTH_SHORT).show();
                }
            });

            btnEditAnswer.setVisibility(View.VISIBLE);
            btnChangeAnswer.setVisibility(View.GONE);
            btnCancelChangeAnswer.setVisibility(View.GONE);
//        changing visibility of et & tv
            tvAnswerText.setVisibility(View.VISIBLE);
            etAnswer.setVisibility(View.GONE);
        }
//        hide keyboard after use
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);
    }

    public void cancelChange(View view){
        if (view.getId() == R.id.btn_cancel_change_question){
            btnEditQuestion.setVisibility(View.VISIBLE);
            btnChangeQuestion.setVisibility(View.GONE);
            btnCancelChangeQuestion.setVisibility(View.GONE);
//        changing visibility of et & tv
            tvQuestionTitle.setVisibility(View.VISIBLE);
            etQuestion.setVisibility(View.GONE);

            tvQuestionTitle.setText(questionObject.getQueTitle());
            etQuestion.setText(questionObject.getQueTitle());
        } else if (view.getId() == R.id.btn_cancel_change_answer){
            btnEditAnswer.setVisibility(View.VISIBLE);
            btnChangeAnswer.setVisibility(View.GONE);
            btnCancelChangeAnswer.setVisibility(View.GONE);
//        changing visibility of et & tv
            tvAnswerText.setVisibility(View.VISIBLE);
            etAnswer.setVisibility(View.GONE);

            tvAnswerText.setText(questionObject.getQueAnswer());
            etAnswer.setText(questionObject.getQueAnswer());
        }
//        hide keyboard after use
        imm.hideSoftInputFromWindow(layout.getWindowToken(), 0);

    }
}