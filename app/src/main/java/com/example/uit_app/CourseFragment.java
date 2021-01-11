package com.example.uit_app;


import androidx.fragment.app.Fragment;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class CourseFragment extends Fragment {

    TextView title, createcourse;

    RecyclerView courseView;
    ArrayList<CourseItem> courseCreated = new ArrayList<CourseItem>();
    PersonalCourseAdapter personalCourseAdapter;

    Button createButton;
    Spinner spinnerChoice;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    UserAccount userAccount;
    SharedPreferences sharedPreferences;

    String joinedCourseResponse;
    boolean joinedFlag = false;

    private static String url = "http://149.28.24.98:9000/join/get-courses-joined-by-user/";

    public CourseFragment() {}

    public CourseFragment(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_course, container, false);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        title = rootView.findViewById(R.id.course_fragment_title);
        courseView = rootView.findViewById(R.id.course_fragment_view);
//        createButton = rootView.findViewById(R.id.createe_course_btn);

        personalCourseAdapter = new PersonalCourseAdapter(courseCreated);
        courseView.setAdapter(personalCourseAdapter);
        courseView.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.VERTICAL, false));

//        createButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), CreateCourse.class);
//                startActivity(intent);
//            }
//        });

        createcourse = rootView.findViewById(R.id.course_text);
        createcourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyCreatedCourse.class);
                startActivity(intent);

//                Fragment fragment = new MyCreatedCourse();
//                FragmentManager fragmentManager = getSupportFragmentManager();
//                fragmentManager.beginTransaction()
//                        .replace(R.id.fragment_container, fragment).commit();
            }
        });

        loadJoinedCourse();

        return rootView;
    }

    private void loadJoinedCourse() {

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).build();

        alertDialog.show();
        iMyService.getJoinedCourse(url+sharedPreferences.getString("id", ""))
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

                        if (joinedFlag) {
                            try {
                                JSONArray ja = new JSONArray(joinedCourseResponse);
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

                                        courseCreated.add(item);
                                        personalCourseAdapter.notifyDataSetChanged();
                                    }
                                    joinedFlag = true;
                                }
                            } catch (JSONException jx) {
                                jx.printStackTrace();
                            }
                        } else {
                            Toast.makeText(getContext(), "No data.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
