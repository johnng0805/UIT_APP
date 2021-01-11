package com.example.uit_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Objects;

import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;

public class ResetPasswordActivity extends AppCompatActivity {

    TextInputEditText tokenET, passwordET;
    Button updateBtn;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;
    boolean flag = false;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        //Get email passed by ForgotPassWordActivity
        email = (String) getIntent().getSerializableExtra("email");

        //Set UI Reference
        tokenET = findViewById(R.id.reset_pass_token_text);
        passwordET = findViewById(R.id.reset_pass_new_text);
        updateBtn = findViewById(R.id.reset_pass_updateBtn);

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidInput()) {
                    resetPassword();
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Please input token and new password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        passwordET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    if (checkValidInput()) {
                        resetPassword();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Please input token and new password", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void resetPassword() {

        updateBtn.setFocusable(false);
        updateBtn.setClickable(false);

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(ResetPasswordActivity.this).build();

        String token = Objects.requireNonNull(tokenET.getText()).toString().trim();
        String password = Objects.requireNonNull(passwordET.getText()).toString().trim();

        alertDialog.show();
        iMyService.resetPassword(email, password, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("active")) {
                            flag = true;
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);
                        Toast.makeText(ResetPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        updateBtn.setFocusable(true);
                        updateBtn.setClickable(true);
                    }

                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);

                        if (flag) {
                            Toast.makeText(ResetPasswordActivity.this, "Reset password successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ResetPasswordActivity.this, "Reset password failed", Toast.LENGTH_SHORT).show();
                            updateBtn.setClickable(true);
                            updateBtn.setFocusable(true);
                        }
                    }
                });
    }

    private boolean checkValidInput() {
        return tokenET.length() != 0 && passwordET.length() != 0;
    }
}