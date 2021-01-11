package com.example.uit_app;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AndroidException;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.CourseItem;
import Retrofit.IMyService;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;

public class PaymentHistoryActivity extends AppCompatActivity {

    RecyclerView courseView;
    PersonalCourseAdapter personalCourseAdapter;
    ArrayList<CourseItem> courseItems;
    boolean flag = false;

    IMyService iMyService;
    Retrofit retrofit;
    SharedPreferences sharedPreferences;

    String url = "http://149.28.24.98:9000/join/get-courses-joined-by-user/";
    String response;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        courseView = findViewById(R.id.payment_history_view);

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        getCourse();
    }

    private void getCourse() {
        iMyService.getJoinedCourse(url + sharedPreferences.getString("id", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        response = s;
                        if (s.contains("name")) {
                            try {
                                JSONArray ja = new JSONArray(s);
                                //int len = Math.min(ja.length(), 8);

                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);

                                    if (!jo.isNull("idCourse")) {
                                        JSONObject joCourse = jo.getJSONObject("idCourse");
                                        CourseItem item = new CourseItem();

                                        item.setID(joCourse.getString("_id"));
                                        item.setTitle(joCourse.getString("name"));
                                        item.setUrl(joCourse.getString("image"));
                                        item.setPercent(jo.getInt("percentCompleted"));
                                        item.setCreateAt(jo.getString("created_at"));

                                        courseItems.add(item);
                                    }
                                    flag = true;
                                    personalCourseAdapter = new PersonalCourseAdapter(courseItems);
                                    courseView.setAdapter(personalCourseAdapter);
                                    courseView.setLayoutManager(new LinearLayoutManager(PaymentHistoryActivity.this,
                                            LinearLayoutManager.VERTICAL, false));
                                }
                            } catch (JSONException jx) {
                                jx.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(PaymentHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        if (!flag) {
                            Toast.makeText(PaymentHistoryActivity.this, response, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
