package com.example.uit_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethod;
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
import retrofit2.http.FormUrlEncoded;

public class ForgotPassWordActivity extends AppCompatActivity {

    TextInputEditText emailET;
    Button sendBtn;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;
    boolean flag = false;

    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass_word);

        //Set UI Reference
        emailET = findViewById(R.id.forgot_pass_input_text);
        sendBtn = findViewById(R.id.forgot_pass_sendBtn);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValidEmail()) {

                    //Hide keyboard
                    emailET.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) Objects
                            .requireNonNull(getApplicationContext())
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(emailET.getWindowToken(), 0);

                    //Send email to server
                    sendEmail();
                } else {
                    Toast.makeText(ForgotPassWordActivity.this, "Please input a valid email address",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        //---On enter key press event---
        emailET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {

                    //Hide keyboard
                    emailET.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) Objects
                            .requireNonNull(getApplicationContext())
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(emailET.getWindowToken(), 0);

                    //Send email to server
                    if (checkValidEmail()) {
                        sendEmail();
                    } else {
                        Toast.makeText(ForgotPassWordActivity.this, "Please input a valid email address", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private void sendEmail() {

        sendBtn.setClickable(false);
        sendBtn.setFocusable(false);

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(ForgotPassWordActivity.this).build();

        String email = Objects.requireNonNull(emailET.getText()).toString().trim();
        alertDialog.show();
        iMyService.forgotPassword(email)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("message")) {
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
                        Toast.makeText(ForgotPassWordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        sendBtn.setClickable(true);
                        sendBtn.setFocusable(true);
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
                            Toast.makeText(ForgotPassWordActivity.this, "A token has been sent to your email", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ForgotPassWordActivity.this, ResetPasswordActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ForgotPassWordActivity.this, "Something's wrong", Toast.LENGTH_SHORT).show();
                            sendBtn.setClickable(true);
                            sendBtn.setFocusable(true);
                        }
                    }
                });
    }

    private boolean checkValidEmail() {
        String email = Objects.requireNonNull(emailET.getText()).toString().trim();
        return email.matches(emailPattern);
    }
}