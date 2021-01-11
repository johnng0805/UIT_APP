package com.example.uit_app;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.stripe.android.ApiResultCallback;
import com.stripe.android.Stripe;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import Retrofit.*;

public class PayActivity extends AppCompatActivity {
    CardInputWidget cardInputWidget;
    Button payBtn;

    SharedPreferences sharedPreferences;
    JSONArray cartArray, cartArraySend;
    JSONObject sendJO = new JSONObject();

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;
    boolean flag = false;
    String result;
    double price = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_pay);

        setUIReference();

        try {
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            cartArray = new JSONArray(sharedPreferences.getString("cartArray", ""));
        } catch (JSONException jx) {
            jx.printStackTrace();
        }

        cartArraySend = new JSONArray();
        for (int i = 0; i < cartArray.length(); i++) {
            try {
                JSONObject jo = new JSONObject();
                JSONObject jNum = new JSONObject();

                jo.put("_id", cartArray.getJSONObject(i).getString("courseID"));
                jo.put("price", cartArray.getJSONObject(i).getString("price"));
                jo.put("discount", cartArray.getJSONObject(i).getString("discount"));
                jo.put("category", cartArray.getJSONObject(i).getString("categoryID"));

                //jNum.put(String.valueOf(i), jo);
                cartArraySend.put(jo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        try {
            //sendJO = new JSONObject();
            sendJO.put("cart", cartArraySend);
            sendJO.put("idUser", sharedPreferences.getString("id", ""));
        } catch (JSONException jx) {
            jx.printStackTrace();
        }

        payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Card card = cardInputWidget.getCard();
                Stripe stripe = new Stripe(PayActivity.this, "pk_test_y8urHXEikr7ysm3tk7uRilcp00aTSdh57w");

                assert card != null;
                stripe.createCardToken(
                        card,
                        new ApiResultCallback<Token>() {
                            @Override
                            public void onSuccess(Token token) {
                                JSONObject tokenJO = new JSONObject();
                                JSONObject cardJO = new JSONObject();
                                try {
                                    cardJO.put("id", token.getCard().getId());
                                    cardJO.put("object", "card");
                                    /*cardJO.put("address_city", token.getCard().getAddressCity());
                                    cardJO.put("address_country", token.getCard().getAddressCountry());
                                    cardJO.put("address_line1", token.getCard().getAddressLine1());
                                    cardJO.put("address_line1_check", token.getCard().getAddressLine1Check());
                                    cardJO.put("address_line2", token.getCard().getAddressLine2());
                                    cardJO.put("address_state", token.getCard().getAddressState());
                                    cardJO.put("address_zip", token.getCard().getAddressZip());
                                    cardJO.put("address_zip_check", token.getCard().getAddressZipCheck());
                                    cardJO.put("brand", token.getCard().getBrand());
                                    cardJO.put("country", token.getCard().getCountry());
                                    cardJO.put("cvc_check", token.getCard().getCvcCheck());
                                    cardJO.put("dynamic_last4", "");
                                    cardJO.put("exp_month", token.getCard().getExpMonth());
                                    cardJO.put("exp_year", token.getCard().getExpYear());
                                    cardJO.put("funding", token.getCard().getFunding());
                                    cardJO.put("last4", token.getCard().getLast4());
                                    cardJO.put("name", "caohoangtu1357@gmail.com");
                                    cardJO.put("tokenization_method", token.getCard().getTokenizationMethod());*/

                                } catch (JSONException jx) {
                                    jx.printStackTrace();
                                }

                                try {
                                    tokenJO.put("id", token.getId());
                                    tokenJO.put("object", "token");
                                    tokenJO.put("card", cardJO);
                                    tokenJO.put("client_ip", "");
                                    tokenJO.put("created", token.getCreated().getTime());
                                    tokenJO.put("type", "card");
                                    tokenJO.put("used", token.getUsed());
                                    tokenJO.put("email", sharedPreferences.getString("email", ""));
                                    tokenJO.put("livemode", token.getLivemode());
                                    tokenJO.put("name", sharedPreferences.getString("name", ""));
                                    tokenJO.put("bank_account", token.getBankAccount());

                                } catch (JSONException jx) {
                                    jx.printStackTrace();
                                }

                                try {
                                    sendJO.put("token", tokenJO);
                                } catch (JSONException jx) {
                                    jx.printStackTrace();
                                }

                                Pay();
                            }

                            @Override
                            public void onError(@NotNull Exception e) {
                                Toast.makeText(PayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }

    private void Pay() {
        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(this).build();
        alertDialog.show();

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), sendJO.toString());
        iMyService.pay(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        flag = true;
                        result = s;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        new Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.dismiss();
                                    }
                                }, 500);
                        Toast.makeText(PayActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @RequiresApi(api = Build.VERSION_CODES.P)
                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);

                        if (flag) {
                            Intent intent = new Intent();
                            intent.putExtra("isPaid", true);

                            setResult(RESULT_OK, intent);
                            finish();
                            Toast.makeText(PayActivity.this, "Payment Successful", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PayActivity.this, "Cannot establish payment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setUIReference() {
        payBtn = findViewById(R.id.card_pay_btn);
        cardInputWidget = findViewById(R.id.card_input_widget);
    }
}
