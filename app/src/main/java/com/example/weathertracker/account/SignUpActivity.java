package com.example.weathertracker.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private String FCMToken;
    private Button btnSendMail;
    private TextInputLayout email, password, passwordAgain;
    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.d("FCMToken", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        Log.d("FCMToken", task.getResult(), task.getException());
                        FCMToken = task.getResult();
                    }
                });
        findId();
        setListener();
    }

    private void findId() {
        email = findViewById(R.id.etEmailLayout);
        password = findViewById(R.id.etPassword);
        passwordAgain = findViewById(R.id.etPasswordAgain);
        btnSendMail = findViewById(R.id.btnSendVerifyEmail);
    }

    private void setListener() {
        btnSendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_text = email.getEditText().getText().toString();
                String password1 = password.getEditText().getText().toString();
                String password2 = passwordAgain.getEditText().getText().toString();

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                    if (password1.length() >= 8) {
                        if (password1.equals(password2)) {
                            retrofitService = RetrofitManager.getInstance().getService();
                            Call<Ack> call = retrofitService.signUp(email_text, password1, FCMToken);
                            call.enqueue(new Callback<Ack>() {
                                @Override
                                public void onResponse(Call<Ack> call, Response<Ack> response) {
                                    if (!response.isSuccessful()) {
                                        Toast.makeText(SignUpActivity.this, "server沒啦", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Ack ack = response.body();
                                        if (ack.getCode() == 200) {
                                            Toast.makeText(SignUpActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();//去信箱收信
                                            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Ack> call, Throwable t) {
                                    Toast.makeText(SignUpActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(SignUpActivity.this, "密碼不一致", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUpActivity.this, "密碼長度須至少8個字元", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUpActivity.this, "信箱格式不正確", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}