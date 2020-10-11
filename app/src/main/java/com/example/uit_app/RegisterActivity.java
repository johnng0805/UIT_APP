package com.example.uit_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    EditText nameET, phoneET, descriptionET, genderET, addressET, emailET, passwordET, passwordconfirmET;
    String name, phone, description, gender, address, email, password, passwordconfirm;
    Button registerBtn;
    TextView loginTV;

    UserAccount userAccount;
    IMyService iMyService;
    SharedPreferences sharedPreferences;

    AlertDialog alertDialog;

    boolean flag = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setUIReference();

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckValidInput()) {
                    Register();
                }
            }
        });

    }

    private void setUIReference() {
        nameET = findViewById(R.id.HoTennText);
        phoneET = findViewById(R.id.PhoneText);
        descriptionET = findViewById(R.id.DescriptText);
        genderET = findViewById(R.id.GenderText);
        addressET = findViewById(R.id.AddressText);
        emailET = findViewById(R.id.EmailText);
        passwordET = findViewById(R.id.PassText);
        passwordconfirmET = findViewById(R.id.CfpText);
        registerBtn = findViewById(R.id.register_button);
        loginTV = findViewById(R.id.LoginBackText);
    }

    private boolean CheckValidInput() {
        boolean valid = true;

        name = nameET.getText().toString();
        phone = phoneET.getText().toString();
        description = descriptionET.getText().toString();
        gender = genderET.getText().toString();
        address = addressET.getText().toString();
        email = emailET.getText().toString();
        password = passwordET.getText().toString();
        passwordconfirm = passwordconfirmET.getText().toString();

        if (name.isEmpty())
        {
            nameET.setError("Please input your name");
            valid = false;
        }
        if (phone.isEmpty() || phone.length() < 7 || phone.length() > 15)
        {
            phoneET.setError("Please input valid phone number");
            valid = false;
        }
        if (description.isEmpty())
        {
            descriptionET.setError("Please input your description");
            valid = false;
        }
        if (gender.isEmpty())
        {
            genderET.setError("Please inout your gender");
            valid = false;
        }
        if (address.isEmpty())
        {
            addressET.setError("Please input your address");
            valid = false;
        }
        if (email.isEmpty() || email.length() < 6 || email.length() > 40)
        {
            emailET.setError("Please input a valid email address");
            valid = false;
        }
        if (password.isEmpty())
        {
            passwordET.setError("Please input your password");
            valid = false;
        }
        else if (passwordconfirm.isEmpty())
        {
            passwordconfirmET.setError("Please re-enter your password");
            valid = false;
        }
        else if (!password.equals(passwordconfirm))
        {
            passwordconfirmET.setError("Password do not match");
        }

        return valid;
    }

    private void Register() {
        registerBtn.setClickable(false);
        registerBtn.setEnabled(false);

        try {
            iMyService.registerUser(name, email, password, phone, address, description, gender)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Observer<Response<String>>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(Response<String> stringResponse) {
                    if (stringResponse.isSuccessful()) {
                        if (stringResponse.body().toString().contains("name")) {
                            String responseString = stringResponse.body().toString();
                            try {
                                JSONObject jo = new JSONObject(responseString);
                                userAccount = new UserAccount(jo.getString("name"),
                                        "",
                                        jo.getString("phone"),
                                        jo.getString("image"),
                                        jo.getString("email"),
                                        stringResponse.headers().get("Auth-token"),
                                        jo.getString("gender"),
                                        jo.getString("description"),
                                        jo.getString("address"),
                                        password,
                                        jo.getString("_id"));

                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                editor.putString("name", userAccount.getHoten());
                                editor.putString("phone", userAccount.getSdt());
                                editor.putString("image", userAccount.getAva());
                                editor.putString("email", userAccount.getMail());
                                editor.putString("token", userAccount.getToken());
                                editor.putString("gender", userAccount.getGioitinh());
                                editor.putString("description", userAccount.getMota());
                                editor.putString("address", userAccount.getDiachia());
                                editor.putString("password", userAccount.getMatkhau());
                                editor.putString("id", userAccount.getID());
                                editor.apply();

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
                public void onError(Throwable e) {
                    new android.os.Handler().postDelayed(
                            new Runnable() {
                                @Override
                                public void run() {
                                    alertDialog.dismiss();
                                }
                            },
                            500
                    );

                    Toast.makeText(RegisterActivity.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    registerBtn.setClickable(true);
                    registerBtn.setEnabled(true);
                }

                @Override
                public void onComplete() {
                    if (flag) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(RegisterActivity.this, "Register failed", Toast.LENGTH_SHORT).show();
                        registerBtn.setEnabled(true);
                        registerBtn.setClickable(true);
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
