package com.example.uit_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import Model.UserAccount;

public class HomeScreenActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    SharedPreferences sharedPreferences;
    public UserAccount userAccount = new UserAccount();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final FragmentManager fragmentManager = getSupportFragmentManager();
        getPreferences();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);

        setUIReference();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = new Fragment();

                switch (item.getItemId()) {
                    case R.id.account_icon:
                        fragment = new AccountFragment(userAccount);
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment).commit();
                return true;
            }
        });
    }

    private void setUIReference() {
        bottomNavigationView = findViewById(R.id.bottom_nav_dock);
    }

    private void getPreferences() {
        userAccount.setHoten(sharedPreferences.getString("name", ""));
        userAccount.setSdt(sharedPreferences.getString("phone", ""));
        userAccount.setNgaysinh("");
        userAccount.setAva("");
        userAccount.setMail(sharedPreferences.getString("email", ""));
        userAccount.setID(sharedPreferences.getString("id", ""));
        userAccount.setToken(sharedPreferences.getString("token", ""));
        userAccount.setGioitinh(sharedPreferences.getString("gender", ""));
        userAccount.setMota(sharedPreferences.getString("description", ""));
        userAccount.setDiachia(sharedPreferences.getString("address", ""));
        userAccount.setMatkhau(sharedPreferences.getString("password", ""));
    }

}
