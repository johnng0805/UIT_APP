package com.example.uit_app;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {
    EditText nameET, phoneET, descriptionET, genderET, addressET, emailET, passwordET;
    String name, phone, description, gender, address, email, password;
    Button registerBtn;
    TextView loginTV;

    UserAccount userAccount;
    IMyService iMyService;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

    }

    private void setUIReference() {
        nameET = findViewById(R.id.HoTennText);
        phoneET = findViewById(R.id.PhoneText);
        descriptionET = findViewById(R.id.DescriptText);
        genderET = findViewById(R.id.GenderText);
        addressET = findViewById(R.id.AddressText);
        emailET = findViewById(R.id.EmailText);
        passwordET = findViewById(R.id.PassText);
        registerBtn = findViewById(R.id.register_button);
        loginTV = findViewById(R.id.LoginBackText);
    }

    private void Register() {
        registerBtn.setClickable(false);
        registerBtn.setEnabled(false);


    }
}
