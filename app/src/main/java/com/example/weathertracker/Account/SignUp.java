package com.example.weathertracker.Account;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.weathertracker.R;
import com.example.weathertracker.Retrofit.Ack;
import com.example.weathertracker.Retrofit.RetrofitManager;
import com.example.weathertracker.Retrofit.RetrofitService;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUp extends AppCompatActivity {

    private Button btn_send_mail;
    private TextInputLayout email, password, password_again;
    private RetrofitService retrofitService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        find_id();
        set_listener();
    }

    private void find_id() {
        email = findViewById(R.id.et_email);
        password = findViewById(R.id.et_password);
        password_again = findViewById(R.id.et_password_again);
        btn_send_mail = findViewById(R.id.btn_send_verify_mail);
    }

    private void set_listener() {
        btn_send_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_text = email.getEditText().getText().toString();
                String password1 = password.getEditText().getText().toString();
                String password2 = password_again.getEditText().getText().toString();

                if (android.util.Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
                    if (password1.length() >= 8) {
                        if (password1.equals(password2)) {
                            retrofitService = RetrofitManager.getInstance().getAPI();
                            Call<Ack> call = retrofitService.send_registeredMail(email_text);
                            call.enqueue(new Callback<Ack>() {
                                @Override
                                public void onResponse(Call<Ack> call, Response<Ack> response) {
                                    if (!response.isSuccessful()) {
                                        Toast.makeText(SignUp.this, "server沒啦", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Ack ack = response.body();
                                        if (ack.getCode() == 200) {
                                            Toast.makeText(SignUp.this, ack.getMsg(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SignUp.this, "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Ack> call, Throwable t) {
                                    Toast.makeText(SignUp.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(SignUp.this, "密碼不一致", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(SignUp.this, "密碼長度必須大於8", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SignUp.this, "信箱格式不正確", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}