package com.example.uit_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import Model.UserAccount;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;


public class AccountFragment extends Fragment {
    Button personalInfoBtn, securityBtn, logoutBtn;
    TextView name, email;
    Boolean flag = false;

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
        securityBtn = rootView.findViewById(R.id.security_btn);
        logoutBtn = rootView.findViewById(R.id.logout_btn);

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

        securityBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AccountPasswordActivity.class);
                intent.putExtra("userAcc", userAccount);
                startActivityForResult(intent, 3);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Logout();
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        return rootView;
    }

    private void Logout() {
        IMyService iMyService;
        final AlertDialog alertDialog;
        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        alertDialog = new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();

        iMyService.userLogout(userAccount.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("success")) {
                            flag = true;
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
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
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
                            Intent intent = new Intent(getContext(), LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getContext(), "Error logging out", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {
                userAccount = (UserAccount) data.getSerializableExtra("userAcc");
                HomeScreenActivity.userAccount = userAccount;
            }
        }
    }
}