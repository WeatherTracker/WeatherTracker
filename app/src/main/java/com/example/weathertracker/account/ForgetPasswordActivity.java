package com.example.weathertracker.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgetPasswordActivity extends AppCompatActivity {

    private TextInputEditText email;
    private Button sendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        email = findViewById(R.id.etEmail);
        sendMail = findViewById(R.id.btnSendForgetEmail);
        sendMail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ForgetPasswordActivity.this, "此功能將日後實裝", Toast.LENGTH_SHORT).show();
//                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
//                    RetrofitService retrofitService = RetrofitManager.getInstance().getService();
//                    Call<Ack> call = retrofitService.sendResetMail(email.getText().toString());
//                    call.enqueue(new Callback<Ack>() {
//                        @Override
//                        public void onResponse(Call<Ack> call, Response<Ack> response) {
//                            if (!response.isSuccessful()) {
//                                Toast.makeText(ForgetPasswordActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Ack ack = response.body();
//                                if (ack.getCode() == 200) {
//                                    Toast.makeText(ForgetPasswordActivity.this, ack.getMsg(), Toast.LENGTH_SHORT).show();//去信箱收信
//                                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                } else {
//                                    Toast.makeText(ForgetPasswordActivity.this, "錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(Call<Ack> call, Throwable t) {
//                            Toast.makeText(ForgetPasswordActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(ForgetPasswordActivity.this, "信箱格式不正確", Toast.LENGTH_SHORT).show();
//                }
            }
        });
    }
}