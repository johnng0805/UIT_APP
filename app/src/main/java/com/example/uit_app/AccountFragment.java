package com.example.uit_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import Model.UserAccount;
import Retrofit.IMyService;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;


public class AccountFragment extends Fragment {
    Button personalInfoBtn, btnSecurity, btnProfilePicture, btnLogOut;
    TextView name, email, password, newpassword, newpasswordconfirm;

    UserAccount userAccount;

    //Change avatar
    String URLDefault="http://149.28.24.98:9000/upload/user_image/";
    private CircleImageView circleImageView;

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
        btnSecurity = rootView.findViewById(R.id.security_btn);
        btnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ChangePaswordActivity.class);
                intent.putExtra("userAcc", userAccount);
                startActivityForResult(intent, 2);
            }
        });

        circleImageView=rootView.findViewById(R.id.imgview_avatar);
        String avurl=URLDefault+userAccount.getAva();
        Picasso.get().load(avurl).placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);

        btnProfilePicture = rootView.findViewById(R.id.btnProfilePicture);
        btnProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChangeAvatarActivity.class);
                intent.putExtra("userAcc", userAccount);
                startActivityForResult(intent,3);
            }
        });

        btnLogOut = rootView.findViewById(R.id.logout_btn);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext())
                        .setTitle("Đăng xuất")
                        .setMessage("Bạn có chắc muốn đăng xuất")
                        .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Logout();

                            }
                        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
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

    private boolean flag = false;
    private void Logout() {

        IMyService iMyService;
        final AlertDialog alertDialog;
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService=retrofitClient.create(IMyService.class);
        alertDialog= new SpotsDialog.Builder().setContext(getContext()).build();
        alertDialog.show();
        iMyService.userLogout(userAccount.getToken()).
                subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>(){
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(String response) {




                        if(response.contains("success")) flag=true;
                        else flag=false;
                        // Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();



                    }

                    @Override
                    public void onError(Throwable e) {
                        new Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if(flag==true)
                        {
                            SharedPreferences sharedPreferences;
                            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.remove("name");
                            editor.remove("phone");
                            editor.remove("image");
                            editor.remove("image");
                            editor.remove("email");
                            editor.remove("gender");
                            editor.remove("description");
                            editor.remove("address");
                            editor.remove("token");
                            editor.remove(("cartArray"));
                            editor.commit();
                            Intent intent = new Intent(getContext(),LoginActivity.class);
                            startActivity(intent);

                        }
                        else
                            Toast.makeText(getContext(), "Đã có lỗi xảy ra", Toast.LENGTH_SHORT).show();

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
                email.setText(userAccount.getMail());

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

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {

                userAccount = (UserAccount) data.getSerializableExtra("usernewAcc");
                ((HomeScreenActivity)getActivity()).userAccount=userAccount;

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("password", userAccount.getMatkhau());

                editor.apply();
            }
        }

        if (requestCode == 3) {
            if (resultCode == Activity.RESULT_OK) {

                userAccount = (UserAccount) data.getSerializableExtra("usernewAcc");
                Picasso.get().load(URLDefault+userAccount.getAva()).placeholder(R.drawable.avatar).error(R.drawable.avatar).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).into(circleImageView);
                ((HomeScreenActivity)getActivity()).userAccount=userAccount;

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("image",userAccount.getAva());
                editor.apply();
            }
        }
    }
}