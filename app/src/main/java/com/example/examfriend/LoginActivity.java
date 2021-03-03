package com.example.examfriend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    //    private ProgressBar progressBar;
    private EditText etPhone , etOtp;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    private String phone,  codeSent;

    @Override
    protected void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
//        sign-out user if back btn pressed
        mAuth.signOut();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        etPhone = findViewById(R.id.et_phone);
        etOtp = findViewById(R.id.et_otp);
        Button btnSendOTP = findViewById(R.id.btn_send_otp);
        Button btnVerify = findViewById(R.id.btn_verify);

        btnVerify.setOnClickListener(view-> verifyOtp());
        btnSendOTP.setOnClickListener(view-> {
            getPhoneNumber();
            sendOtp();
        });
    }

    private void getPhoneNumber() {
        if (TextUtils.isEmpty(etPhone.getText())){
            etPhone.setError("Please enter phone number!");
            return;
        }
        phone = etPhone.getText().toString();
        if (phone.length()<10) {
            etPhone.setError("Enter valid phone number!");
            return;
        }
        phone = "+91"+phone;
    }


    public void sendOtp() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallback
        );

    }

    public void verifyOtp() {
        //        otp entered and button clicked for verification
        if (TextUtils.isEmpty(etOtp.getText())){
            etOtp.setError("Please enter OTP!");
            return;
        }
        String codeEntered = etOtp.getText().toString();

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(codeSent, codeEntered);

        signInWithCredentials(credential);

    }

    //    CALLBACK method
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
//            verification completed without sending OTP
//            direct sign in
            signInWithCredentials(phoneAuthCredential);
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
//            verification of number failed
            Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            codeSent = s;
            Toast.makeText(LoginActivity.this, "Code sent successfully", Toast.LENGTH_SHORT).show();
        }
    };

    //    SIGN IN method
    private void signInWithCredentials(PhoneAuthCredential credential){
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
//                    Log.d(TAG, "onComplete: isLoggedInBefore: "+ isAlreadyLoggedIn);
                    if (task.isSuccessful()){
                        DetailsDialog detailsDialog = new DetailsDialog(this, phone);
                        detailsDialog.show();
                    } else {
                        Toast.makeText(LoginActivity.this, "Something went wrong! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}