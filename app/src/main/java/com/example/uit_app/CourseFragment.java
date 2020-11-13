package com.example.uit_app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Observable;

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

    TextView title;

    RecyclerView courseView;
    ArrayList<CourseItem> courseCreated;

    Button createButton;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    UserAccount userAccount;

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

        title = rootView.findViewById(R.id.course_fragment_title);
        courseView = rootView.findViewById(R.id.course_fragment_view);
        createButton = rootView.findViewById(R.id.create_course_btn);

        loadJoinedCourse();

        return rootView;
    }

    private void loadJoinedCourse() {

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).build();

        iMyService.getJoinedCourse(url+userAccount.getID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        if (!s.isEmpty()) {
                            debugFunc(s);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void debugFunc(String s) {
        String temp = s;
    }
}
