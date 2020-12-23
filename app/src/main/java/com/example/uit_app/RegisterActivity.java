package com.example.uit_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    EditText txtHoTen, txtPhone, txtDescription, txtGender, txtAddress, txtEmail, txtPassword, txtPasswordconfirm;
    Button bntRegister;
    TextView tvLogin;

    String name, phone, description, gender, address, email, password, passwordconfirm;

    UserAccount userAccount;
    IMyService iMyService;
    SharedPreferences sharedPreferences;
    AlertDialog alertDialog;
    CompositeDisposable compositeDisposable =new CompositeDisposable();

    boolean flag = false;
    String serverResponse;

    @Override
    protected void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUIReference();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        alertDialog = new SpotsDialog.Builder().setContext(this).build();
        iMyService=retrofitClient.create(IMyService.class);

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        bntRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckValidInput()) {
                    SignUp();
                }
            }
        });
    }

    private void SignUp() {
        bntRegister.setClickable(false);
        bntRegister.setEnabled(false);

        alertDialog.show();
        iMyService.registerUser(name, email, password, phone, address, description, gender)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<String> stringResponse) {
                        if (stringResponse.isSuccessful()) {
                            assert stringResponse.body() != null;
                            if (stringResponse.body().contains("name")) {
                                String responseString = stringResponse.body().toString();
                                try {
                                    JSONObject jo = new JSONObject(responseString);
                                    userAccount = new UserAccount(jo.getString("name"), "",
                                            jo.getString("phone"),
                                            jo.getString("image"),
                                            jo.getString("email"),
                                            stringResponse.headers().get("Auth-token"),
                                            jo.getString("gender"),
                                            jo.getString("description"),
                                            jo.getString("address"),
                                            password,
                                            jo.getString("_id")
                                    );


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
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        flag = false;

                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);


                        if (flag) {
                            Intent intent = new Intent(RegisterActivity.this, ActiveAccountActivity.class);
                            intent.putExtra("userAcc", userAccount);
                            intent.putExtra("change",0);
                            startActivity(intent);
                            //New Intent for ActiveAccountActivity
                            //...

                        } else {
                            Toast.makeText(RegisterActivity.this, "Something's wrong", Toast.LENGTH_SHORT).show();
                            bntRegister.setClickable(true);
                            bntRegister.setEnabled(true);
                        }
                    }
                });
    }

    private boolean CheckValidInput() {
        boolean valid = true;

//        txtHoTen, txtPhone, txtDescription, txtGender, txtAddress, txtEmail, txtPassword, txtPasswordconfirm
        name = txtHoTen.getText().toString();
        phone = txtPhone.getText().toString();
        description = txtDescription.getText().toString();
        gender = txtGender.getText().toString();
        address = txtAddress.getText().toString();
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        passwordconfirm = txtPasswordconfirm.getText().toString();

        if (name.isEmpty())
        {
            txtHoTen.setError("Please input your name");
            valid = false;
        }
        if (phone.isEmpty() || phone.length() < 7 || phone.length() > 15)
        {
            txtPhone.setError("Please input valid phone number");
            valid = false;
        }
        if (description.isEmpty())
        {
            txtDescription.setError("Please input your description");
            valid = false;
        }
        if (gender.isEmpty())
        {
            txtGender.setError("Please inout your gender");
            valid = false;
        }
        if (address.isEmpty())
        {
            txtAddress.setError("Please input your address");
            valid = false;
        }
        if (email.isEmpty() || email.length() < 6 || email.length() > 40)
        {
            txtEmail.setError("Please input a valid email address");
            valid = false;
        }
        if (password.isEmpty())
        {
            txtPassword.setError("Please input your password");
            valid = false;
        }
        else if (passwordconfirm.isEmpty())
        {
            txtPasswordconfirm.setError("Please re-enter your password");
            valid = false;
        }
        else if (!password.equals(passwordconfirm))
        {
            txtPasswordconfirm.setError("Password do not match");
        }

        return valid;
    }

    private void setUIReference() {
        txtHoTen = findViewById(R.id.txtActToken);
        txtPhone = findViewById(R.id.PhoneText);
        txtDescription = findViewById(R.id.DescriptText);
        txtGender = findViewById(R.id.GenderText);
        txtAddress = findViewById(R.id.AddressText);
        txtEmail = findViewById(R.id.EmailText);
        txtPassword = findViewById(R.id.PassText);
        txtPasswordconfirm = findViewById(R.id.CfpText);
        bntRegister = findViewById(R.id.btnConfirm);
        tvLogin = findViewById(R.id.LoginBackText);
    }
}


