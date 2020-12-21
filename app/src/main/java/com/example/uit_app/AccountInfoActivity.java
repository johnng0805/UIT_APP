package com.example.uit_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import Model.UserAccount;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import Retrofit.*;
import Retrofit.RetrofitClient;

public class AccountInfoActivity extends AppCompatActivity {

    EditText nameET, genderET, descriptionET, phoneET, addressET;
    Button saveBtn;

    UserAccount userAccount;
    IMyService iMyService;
    AlertDialog alertDialog;

    SharedPreferences sharedPreferences;
    boolean flag = false;

    String name, gender, description, phone, address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        userAccount = (UserAccount) getIntent().getSerializableExtra("userAcc");
        setUIReferences();
        setPreferences();

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        userAccount = (UserAccount) getIntent().getSerializableExtra("userAcc");
        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getInfo();
                changeUserProfile();
            }
        });
    }

    private void setUIReferences() {
        nameET = findViewById(R.id.edtPassword);
        genderET = findViewById(R.id.edtNewPassword);
        descriptionET = findViewById(R.id.edtNewPasswordConfirm);
        phoneET = findViewById(R.id.aa_phone_text);
        addressET = findViewById(R.id.aa_address_text);

        saveBtn = findViewById(R.id.btnSavePassword);
    }

    private void setPreferences() {
        nameET.setText(userAccount.getHoten());
        genderET.setText(userAccount.getGioitinh());
        phoneET.setText(userAccount.getSdt());
        descriptionET.setText(userAccount.getMota());
        addressET.setText(userAccount.getDiachia());
    }

    private void getInfo() {
        name = nameET.getText().toString();
        gender = genderET.getText().toString();
        phone = phoneET.getText().toString();
        description = descriptionET.getText().toString();
        address = addressET.getText().toString();
    }

    private void changeUserProfile() {
        saveBtn.setClickable(false);
        saveBtn.setFocusable(false);

        try {
            alertDialog.show();
            iMyService.changeProfile(name, phone, address, description, gender, userAccount.getToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<String>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull Response<String> stringResponse) {
                            if (stringResponse.isSuccessful()) {
                                if (stringResponse.body().toString().contains("success")) {
                                    String response = stringResponse.body().toString();

                                    try {
                                        JSONObject jo = new JSONObject(response);

                                        userAccount.setHoten(name);
                                        userAccount.setGioitinh(gender);
                                        userAccount.setMota(description);
                                        userAccount.setSdt(phone);
                                        userAccount.setDiachia(address);

                                        flag = true;

                                    } catch (JSONException jx) {
                                        jx.printStackTrace();
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
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                        }
                                    }, 500);

                            Toast.makeText(AccountInfoActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            saveBtn.setClickable(true);
                            saveBtn.setFocusable(true);
                        }

                        @Override
                        public void onComplete() {
                            new android.os.Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                }
                            }, 500);

                            if (flag) {
                                Toast.makeText(AccountInfoActivity.this, "Success", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("userNewAcc", userAccount);

                                setResult(Activity.RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(AccountInfoActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                saveBtn.setClickable(true);
                                saveBtn.setFocusable(true);
                            }
                        }
                    });

        } catch (Exception e) {
            e.getStackTrace();
        }
    }
}