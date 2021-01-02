package com.example.uit_app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import Model.UserAccount;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.RetrofitClient;

public class ActiveAccountActivity extends AppCompatActivity {

    EditText EmailEdtText, textActToken;
    Button btnConfirm;


    String ActiveCode = "";
    String Mail = "";

    UserAccount userAccount = new UserAccount();
    CompositeDisposable compositeDisposable =new CompositeDisposable();
    IMyService iMyService;
    AlertDialog alertDialog;
    boolean activated = false;
    boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_account);

        userAccount = (UserAccount) getIntent().getSerializableExtra("userAcc");
        setUIReference();
        alertDialog = new SpotsDialog.Builder().setContext(this).build();
        Retrofit retrofitClient= RetrofitClient.getInstance();
        iMyService = retrofitClient.create(IMyService.class);
        Mail = userAccount.getMail();
        //Toast.makeText(this, mail, Toast.LENGTH_SHORT).show();
        Toast.makeText(ActiveAccountActivity.this, "Authenticate token has been sent to your email", Toast.LENGTH_LONG).show();

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckValidInput()) {
                    Active();
                }
            }
        });
    }

    private void Active() {
        alertDialog.show();;
        iMyService.activeAccUser(userAccount.getMail(), ActiveCode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        activated = true;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        //JSONObject jObjError = new JSONObject(e.)

                        Toast.makeText(ActiveAccountActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    public void run() {
                                        alertDialog.dismiss();

                                    }
                                }, 500);

                        if (activated) {
                            Toast.makeText(ActiveAccountActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(ActiveAccountActivity.this, LoginActivity.class);
                            intent.putExtra("userAcc", userAccount);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ActiveAccountActivity.this, "Register Unsuccessful", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private boolean CheckValidInput(){
        ActiveCode = textActToken.getText().toString();
        if (ActiveCode.isEmpty()) {
            valid=false;
            Toast.makeText(this, "Please enter token number", Toast.LENGTH_SHORT).show();}
        else valid=true;
        return valid;
    }

    private void setUIReference() {
        textActToken = findViewById(R.id.txtActToken);
        btnConfirm = findViewById(R.id.btnConfirm);
    }
}