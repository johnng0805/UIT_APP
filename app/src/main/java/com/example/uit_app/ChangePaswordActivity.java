package com.example.uit_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.json.JSONException;
import org.json.JSONObject;

import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
public class ChangePaswordActivity extends AppCompatActivity {

    EditText edtPassword, edtNewPassword, edtNewPasswordConfirm;
    Button btnSavePassword;

    String Password, newPassword, newPasswordConfirm;

    Toolbar userPassTB;
    IMyService iMyService;
    AlertDialog alertDialog;
    UserAccount userAccount=new UserAccount();

    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);

        setUIReference();
        Retrofit retrofitClient= RetrofitClient.getInstance();

        iMyService = retrofitClient.create(IMyService.class);
        userAccount = (UserAccount) getIntent().getSerializableExtra("userAcc");
        alertDialog = new SpotsDialog.Builder().setContext(this).build();

//        edtPassword.setText(userAccount.getMatkhau());// lát code xong xoá
//        edtNewPassword.setText("Abcd1234");
//        edtNewPasswordConfirm.setText("Abcd1234");
        ActionToolBar();
    }

    private void ActionToolBar() {
        setSupportActionBar(userPassTB);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        userPassTB.setTitleTextColor(-1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userPassTB.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidInput())
                    changePass();
            }
        });
    }

    private void changePass() {
        btnSavePassword.setClickable(false);
        btnSavePassword.setEnabled(false);

        alertDialog.show();
        iMyService.changePass(Password,newPassword,userAccount.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Response<String> stringResponse) {

                        if(stringResponse.isSuccessful()){


                            if(stringResponse.body().toString().contains("success"))
                            {
                                String responseString=stringResponse.body().toString();
                                try {
                                    JSONObject jo = new JSONObject(responseString);

                                    userAccount.setMatkhau(newPassword);

                                    flag = true;
                                }
                                catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            else {
                                flag = false;
                            }
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);
                        Toast.makeText(ChangePaswordActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

                        if(flag == true)
                        { Toasty.success(ChangePaswordActivity.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
                            final Intent data = new Intent();

                            // Truyền data vào intent
                            data.putExtra("usernewAcc", userAccount);

                            // Đặt resultCode là Activity.RESULT_OK to
                            // thể hiện đã thành công và có chứa kết quả trả về
                            setResult(Activity.RESULT_OK, data);

                            // gọi hàm finish() để đóng Activity hiện tại và trở về MainActivity.
                            finish();
                        }
                        else
                            Toast.makeText(ChangePaswordActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        btnSavePassword.setEnabled(true);
                        btnSavePassword.setClickable(true);

                    }
                });
    }

    private boolean checkValidInput() {
        boolean valid=true;
        Password = edtPassword.getText().toString();
        newPassword = edtNewPassword.getText().toString();
        newPasswordConfirm = edtNewPasswordConfirm.getText().toString();
        if(Password.isEmpty()||!Password.equals(userAccount.getMatkhau()))
        {
            valid=false;
            Toast.makeText(this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
            return valid;
        }
        else
        {
            valid=true;
        }
        if(newPassword.isEmpty() || newPassword.length() <8 || newPassword.length()>16)
        {
            valid=false;
            Toast.makeText(this, "Mật khẩu mới phải từ 8 đến 16 ký tự", Toast.LENGTH_SHORT).show();
            return valid;
        }
        else{
            valid=true;
        }
        if(!newPasswordConfirm.equals(newPassword))
        {
            valid=false;
            Toast.makeText(this, "Xác nhận mật khẩu không khóp", Toast.LENGTH_SHORT).show();
            return valid;


        }
        else{
            valid=true;
        }
        return valid;
    }

    private void setUIReference() {
        userPassTB = findViewById(R.id.userPassTB);
        edtPassword = findViewById(R.id.txtPassword);
        edtNewPassword = findViewById(R.id.txtNewPassword);
        edtNewPasswordConfirm = findViewById(R.id.txtNewPasswordConfirm);
        btnSavePassword = findViewById(R.id.btnSavePassword);
    }
}