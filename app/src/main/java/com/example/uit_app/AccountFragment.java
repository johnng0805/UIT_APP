package com.example.uit_app;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import Model.UserAccount;


public class AccountFragment extends Fragment {
    Button personalInfoBtn;
    TextView name, email;

    UserAccount userAccount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public AccountFragment(UserAccount user) {
        this.userAccount = user;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);

        name = rootView.findViewById(R.id.account_name);
        email = rootView.findViewById(R.id.account_email);
        personalInfoBtn = rootView.findViewById(R.id.person_information_btn);

        name.setText(userAccount.getHoten());
        email.setText(userAccount.getMail());

        personalInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AccountInfoActivity.class);
                intent.putExtra("userAcc", userAccount);
                startActivityForResult(intent, 1);
            }
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                userAccount = (UserAccount) data.getSerializableExtra("userNewAcc");
                HomeScreenActivity.userAccount = userAccount;
                name.setText(userAccount.getHoten());

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("name", userAccount.getHoten());
                editor.putString("gender", userAccount.getGioitinh());
                editor.putString("description", userAccount.getMota());
                editor.putString("phone", userAccount.getSdt());
                editor.putString("address", userAccount.getDiachia());

                editor.apply();
            }
        }
    }
}