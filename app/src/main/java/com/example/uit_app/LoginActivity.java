package com.example.uit_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends AppCompatActivity {
    Button loginBtn;
    TextView registerTV, forgotpasswordTV;
    EditText emailEditText, passwordEditText;
    String email, password;

    AlertDialog alertDialog;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    IMyService iMyService;
    SharedPreferences sharedPreferences;
    UserAccount userAccount;

    boolean flag = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setUIReference();

        //emailEditText.setText("luongkhoadang13012000@gmail.com");
        //passwordEditText.setText("Abcd1234");

        Retrofit retrofitClient = RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(this).build();



        registerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckValidInput())
                    Login();
            }
        });

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    passwordEditText.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getApplicationContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE));
                    inputMethodManager.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
                    if (CheckValidInput()) {
                        Login();
                        return true;
                    }
                }
                return false;
            }
        });

        forgotpasswordTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgotPassWordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void Login() {
        loginBtn.setClickable(false);
        loginBtn.setEnabled(false);

        try {
            alertDialog.show();
            iMyService.loginUser(email, password)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Response<String>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(@NotNull Response<String> stringResponse) {
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
                                                jo.getString("_id"),
                                                stringResponse.headers().get("Auth-token"),
                                                jo.getString("gender"),
                                                jo.getString("description"),
                                                jo.getString("address"),
                                                password);

                                        SharedPreferences.Editor editor = sharedPreferences.edit();

                                        if (!sharedPreferences.getString("email", "").equals(userAccount.getMail())) {
                                            editor.putBoolean("diffUser", true);
                                        } else {
                                            editor.putBoolean("diffUser", false);
                                        }

                                        editor.putBoolean("diffUser", true);
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

                            Toast.makeText(LoginActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            loginBtn.setClickable(true);
                            loginBtn.setEnabled(true);
                        }

                        @Override
                        public void onComplete() {
                            new android.os.Handler().postDelayed(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            alertDialog.dismiss();
                                        }
                                    }
                                    , 500);

                            if (flag) {
                                Intent intent = new Intent(LoginActivity.this,
                                        HomeScreenActivity.class);
                                intent.putExtra("userAcc", userAccount);
                                intent.putExtra("change", 0);
                                startActivity(intent);
                            } else {
                                Toast.makeText(LoginActivity.this,
                                        "Email or password is incorrect",
                                        Toast.LENGTH_SHORT).show();
                                loginBtn.setClickable(true);
                                loginBtn.setEnabled(true);
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUIReference() {
        loginBtn = findViewById(R.id.login_btn);
        registerTV = findViewById(R.id.register_text);
        emailEditText = findViewById(R.id.text_name_edit);
        passwordEditText = findViewById(R.id.text_password_edit);
        forgotpasswordTV = findViewById(R.id.forgot_password_text);
    }

    private boolean CheckValidInput() {
        boolean valid = true;
        email = emailEditText.getText().toString();
        password = passwordEditText.getText().toString();

        if (email.isEmpty() || email.length() < 6 || email.length() > 40) {
            emailEditText.setError("From 6 to 40 characters");
            valid = false;
        } else {
            emailEditText.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 16) {
            passwordEditText.setError("Password must be 8 - 16 characters");
            valid = false;
        } else {
            passwordEditText.setError(null);
        }
        return valid;
    }
}
