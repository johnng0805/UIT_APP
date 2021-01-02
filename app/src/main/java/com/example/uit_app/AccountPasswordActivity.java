package com.example.uit_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.*;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;

public class AccountPasswordActivity extends AppCompatActivity {
    UserAccount userAccount;
    EditText oldPass, newPass, newPassConfirm;
    String oldPassString, newPassString;
    Button saveBtn;
    File file;
    boolean flag = false;

    IMyService iMyService;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        userAccount = (UserAccount) getIntent().getSerializableExtra("userAcc");
        setUIReference();

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    oldPassString = oldPass.getText().toString();
                    newPassString = newPassConfirm.getText().toString();
                    ChangePassword();
            }
        });
    }

    private void setUIReference() {
        oldPass = findViewById(R.id.security_old_text);
        newPass = findViewById(R.id.security_new_text);
        newPassConfirm = findViewById(R.id.security_confirm_text);
        saveBtn = findViewById(R.id.security_save_button);
    }

    private boolean checkValidInput() {
        if (oldPass.getText() == null ||
                newPass.getText() == null ||
                newPassConfirm.getText() == null) {
            return false;
        } else if (!oldPass.getText().toString().equals(userAccount.getMatkhau())) {
            return false;
        } else return newPass.getText() == newPassConfirm.getText();
    }

    private void ChangePassword() {
        saveBtn.setClickable(false);
        saveBtn.setFocusable(false);

        try {
            alertDialog.show();
            iMyService.changePassword(oldPassString, newPassString, userAccount.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<String>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Response<String> stringResponse) {
                            if (stringResponse.isSuccessful()) {
                                if (stringResponse.body().contains("success")) {
                                    try {
                                        JSONObject object = new JSONObject(stringResponse.body().toString());
                                        userAccount.setMatkhau(newPassString);
                                        flag = true;
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    flag = false;
                                }
                            } else {
                                flag = false;
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
                            Toast.makeText(AccountPasswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            saveBtn.setClickable(true);
                            saveBtn.setFocusable(true);
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
                                Toast.makeText(AccountPasswordActivity.this, "Success", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                intent.putExtra("userAcc", userAccount);

                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(AccountPasswordActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                saveBtn.setClickable(true);
                                saveBtn.setFocusable(true);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
