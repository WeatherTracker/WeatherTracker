package com.example.weathertracker.account;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.weathertracker.MainActivity;
import com.example.weathertracker.R;
import com.example.weathertracker.retrofit.Ack;
import com.example.weathertracker.retrofit.RetrofitManager;
import com.example.weathertracker.retrofit.RetrofitService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.royrodriguez.transitionbutton.TransitionButton;

import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private GoogleSignInOptions gso;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    private SignInButton signInButton;
    private String TAG = "GSO";
    private int RC_SIGN_IN = 200;
    private Button btnForget, btnSignUp;
    private TransitionButton btnLogin;
    public static Double latitude = 0.0, longitude = 0.0;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private TextInputEditText etLoginEmail, etLoginPassword;
    private String FCMToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
        findID();
        setListener();


        SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
        sharedPreferences.edit().putFloat("Longitude", (float) 25.03746).apply();
        sharedPreferences.edit().putFloat("Latitude", (float) 121.564558).apply();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    System.out.println("123321" + location.getLongitude() + location.getLatitude());
                    SharedPreferences sharedPreferences = getSharedPreferences("data", MODE_PRIVATE);
                    sharedPreferences.edit().putFloat("Longitude", (float) location.getLongitude()).apply();
                    sharedPreferences.edit().putFloat("Latitude", (float) location.getLatitude()).apply();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44) {
            System.out.println("i am granted");
            getLocation();
        }
    }

    private void findID() {
        signInButton = findViewById(R.id.btnSignIn);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        btnLogin = findViewById(R.id.btnLogin);
        btnForget = findViewById(R.id.btnForget);
        btnSignUp = findViewById(R.id.btnSignUp);
        etLoginEmail = findViewById(R.id.etLoginEmail);
        etLoginPassword = findViewById(R.id.etLoginPassword);
    }

    private void setListener() {
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(LoginActivity.this, "此功能將日後實裝", Toast.LENGTH_SHORT).show();
                signIn();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(LoginActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    // Start the loading animation when the user tap the button
                    btnLogin.startAnimation();
                    RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                    Call<Ack> call = retrofitService.signIn(etLoginEmail.getText().toString(), etLoginPassword.getText().toString(), FCMToken);
                    call.enqueue(new Callback<Ack>() {
                        @Override
                        public void onResponse(Call<Ack> call, Response<Ack> response) {
                            if (!response.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                                btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                            } else {
                                Ack ack = response.body();

                                // Choose a stop animation if your call was successful or not
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (ack.getCode() == 200) {
                                            SharedPreferences pref = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                                            pref.edit()
                                                    .putString("userId", ack.getMsg())
                                                    .apply();
                                            System.out.println(ack.getMsg());
                                            btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                                @Override
                                                public void onAnimationStopEnd() {
                                                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            });
                                        } else {
                                            btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                                            Toast.makeText(LoginActivity.this, "錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }, 1000);
                            }
                        }

                        @Override
                        public void onFailure(Call<Ack> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                            btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                        }
                    });
                } else {
                    Toast.makeText(LoginActivity.this, "請允許取得定位功能，否則應用程式無法執行", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            myGoogleSignIn(account.getEmail());
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            myGoogleSignUp(account.getEmail());

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed '''code'''=" + e.getStatusCode());
            Toast.makeText(this, "登入失敗，請稍後再試", Toast.LENGTH_SHORT).show();
        }
    }

    private void myGoogleSignIn(String email) {
        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        Call<Ack> call = retrofitService.googleSignIn(encoder(email), FCMToken);
        call.enqueue(new Callback<Ack>() {
            @Override
            public void onResponse(Call<Ack> call, Response<Ack> response) {
                if (response.isSuccessful()) {
                    Ack ack = response.body();
                    SharedPreferences pref = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                    pref.edit()
                            .putString("userId", ack.getMsg())
                            .apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Ack> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void myGoogleSignUp(String email) {
        RetrofitService retrofitService = RetrofitManager.getInstance().getService();
        Call<Ack> call = retrofitService.googleSignUp(encoder(email), FCMToken);
        call.enqueue(new Callback<Ack>() {
            @Override
            public void onResponse(Call<Ack> call, Response<Ack> response) {
                if (response.isSuccessful()) {
                    Ack ack = response.body();
                    SharedPreferences pref = getSharedPreferences("sharedPreferences", MODE_PRIVATE);
                    pref.edit()
                            .putString("userId", ack.getMsg())
                            .apply();
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "伺服器錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Ack> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "連線錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String encoder(String email) {
        String fooResult = "";
        try {
            final byte[] textByte = email.getBytes(StandardCharsets.UTF_8);
            String foo = Base64.encodeToString(textByte, Base64.NO_WRAP);
            final byte[] textByte2 = foo.getBytes(StandardCharsets.UTF_8);
            fooResult = Base64.encodeToString(textByte2, Base64.NO_WRAP);
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "系統錯誤，請稍後再試", Toast.LENGTH_SHORT).show();
        }
        return fooResult;
    }

}