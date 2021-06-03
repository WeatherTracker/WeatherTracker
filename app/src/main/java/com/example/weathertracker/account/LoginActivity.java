package com.example.weathertracker.account;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
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
import com.royrodriguez.transitionbutton.TransitionButton;

import java.io.IOException;
import java.util.Locale;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        findID();
        setListener();


        SharedPreferences sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
        sharedPreferences.edit().putFloat("Longitude" , 24).apply();
        sharedPreferences.edit().putFloat("Latitude" , 121).apply();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (ActivityCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        } else {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }


        System.out.println("hi");
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location!=null){
                    System.out.println("123321"+location.getLongitude()+location.getLatitude());
                    SharedPreferences sharedPreferences = getSharedPreferences("data" , MODE_PRIVATE);
                    sharedPreferences.edit().putFloat("Longitude" , (float) location.getLongitude()).apply();
                    sharedPreferences.edit().putFloat("Latitude" , (float) location.getLatitude()).apply();
                }

            }
        });
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
                signIn();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the loading animation when the user tap the button
                btnLogin.startAnimation();
                RetrofitService retrofitService = RetrofitManager.getInstance().getService();
                Call<Ack> call = retrofitService.signIn(etLoginEmail.getText().toString(), etLoginPassword.getText().toString());
                call.enqueue(new Callback<Ack>() {
                    @Override
                    public void onResponse(Call<Ack> call, Response<Ack> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "server沒啦", Toast.LENGTH_SHORT).show();
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
                                        Toast.makeText(LoginActivity.this, "錯誤代碼: " + ack.getCode() + ",錯誤訊息: " + ack.getMsg(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onFailure(Call<Ack> call, Throwable t) {
                        Toast.makeText(LoginActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                        btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                    }
                });

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


//    @Override
//    protected void onStart() {
//        super.onStart();
//        account = GoogleSignIn.getLastSignedInAccount(this);
//        if (account != null) {
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//        }
//
////        updateUI(account);
//    }

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
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed '''code'''=" + e.getStatusCode());
//            updateUI(null);
            Toast.makeText(this, "登入失敗，請稍後再試", Toast.LENGTH_SHORT).show();
        }
    }

}