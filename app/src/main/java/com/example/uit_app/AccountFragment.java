package com.example.uit_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

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
                startActivity(intent);
            }
        });
        return rootView;
    }
}