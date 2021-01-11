package com.example.uit_app;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import com.example.uit_app.R;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.CourseItem;
import Model.UserAccount;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;

//import android.support.v4.app.*;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;


public class MyCreatedCourse extends FragmentActivity {

    TextView title, txtCourse;

    RecyclerView courseView;
    ArrayList<CourseItem> courseCreated = new ArrayList<CourseItem>();
    CreatedCourseAdapter createdCourseAdapter;

    Button createButton;
    Spinner spinnerChoice;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    UserAccount userAccount;
    SharedPreferences sharedPreferences;

    String joinedCourseResponse;
    boolean joinedFlag = false;

    private static String url = "http://149.28.24.98:9000/course/getby-iduser/";

    public MyCreatedCourse() {
    }

    public MyCreatedCourse(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_created_course);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        title = findViewById(R.id.course_fragment_title);
        courseView = findViewById(R.id.created_course_list);

        createdCourseAdapter = new CreatedCourseAdapter(courseCreated);
        courseView.setAdapter(createdCourseAdapter);



        courseView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL, false));

        createButton = findViewById(R.id.created_course_btn);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCreatedCourse.this, CreateCourse.class);
                startActivity(intent);
            }
        });

        loadCreatedCourse();




        //Chinh sua khi la Activity
//       setContentView(R.layout.activity_created_course);
//        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
//        title = findViewById(R.id.course_fragment_title);
//        courseView = findViewById(R.id.course_fragment_view);
//
//        createButton = findViewById(R.id.created_course_btn);
//        createButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MyCreatedCourse.this, CreateCourse.class);
//                startActivity(intent);
//            }
//        });
        //Chinh sua khi la Activity
   }




        //Chinh sua khi la Fragment
//        @Nullable
//        @Override
//        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//            final View rootView = inflater.inflate(R.layout.activity_my_created_course, container, false);
//
//            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//
//            title = rootView.findViewById(R.id.course_fragment_title);
//            courseView = rootView.findViewById(R.id.course_fragment_view);
//            createButton = rootView.findViewById(R.id.createe_course_btn);
//
//            personalCourseAdapter = new PersonalCourseAdapter(courseCreated);
//            courseView.setAdapter(personalCourseAdapter);
//            courseView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
//                    LinearLayoutManager.VERTICAL, false));
//
//            createButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MyCreatedCourse.this, CreateCourse.class);
//                    startActivity(intent);
//                }
//            });
//
//
//
//            loadJoinedCourse();
//            return rootView;
//
//    }

    private void loadCreatedCourse(){

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(getApplicationContext()).build();

        iMyService.getCreatedCourse(url+sharedPreferences.getString("id", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        joinedCourseResponse = s;
                        joinedFlag = true;
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);
                        Toast.makeText(MyCreatedCourse.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);

                        if (joinedFlag) {
                            try {
                                JSONArray ja = new JSONArray(joinedCourseResponse);
                                //int len = Math.min(ja.length(), 8);

                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject joCourse = ja.getJSONObject(i);

                                    {

                                        CourseItem item = new CourseItem();

                                        item.setID(joCourse.getString("_id"));
                                        item.setTitle(joCourse.getString("name"));
                                        item.setUrl(joCourse.getString("image"));
                                        item.setCreateAt(joCourse.getString("created_at"));

                                        courseCreated.add(item);
                                        createdCourseAdapter.notifyDataSetChanged();
                                    }
                                    joinedFlag = true;
                                }
                            } catch (JSONException jx) {
                                jx.printStackTrace();
                            }
                        } else {
                            Toast.makeText(MyCreatedCourse.this, "No data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}

