package com.example.uit_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.net.MailTo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.JsonIOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import Model.CategoryItem;
import Model.CourseItem;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;
import retrofit2.http.Url;

public class SearchFragment extends Fragment {

    EditText searchString;
    RecyclerView searchResult, categoriesView;
    Boolean flag = false;

    ArrayList<CourseItem> courseItems = new ArrayList<>();
    PersonalCourseAdapter personalCourseAdapter;

    ArrayList<CategoryItem> categoryItems = new ArrayList<>();
    CategoryItemAdapter categoryItemAdapter;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    InputMethodManager inputMethodManager;

    public SearchFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        searchString = rootView.findViewById(R.id.search_input);
        searchResult = rootView.findViewById(R.id.search_result_view);
        categoriesView = rootView.findViewById(R.id.search_category_view);
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).build();

        searchString.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCourse();
                    return true;
                }
                return false;
            }
        });

        loadAllCategories();
        return rootView;
    }

    private void loadAllCategories() {

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        iMyService.getAllCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        if (s.contains("_id")) {
                            try {
                                JSONArray ja = new JSONArray(s);

                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    categoryItems.add(new CategoryItem(
                                            jo.getString("name"),
                                            jo.getString("_id"),
                                            jo.getString("image")
                                    ));
                                }

                                categoryItemAdapter = new CategoryItemAdapter(getActivity(), categoryItems);
                                categoriesView.setAdapter(categoryItemAdapter);
                                categoriesView.setLayoutManager(new LinearLayoutManager(getActivity(),
                                        LinearLayoutManager.HORIZONTAL, false));

                            } catch (JSONException jx) {
                                jx.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void searchCourse() {
        searchString.clearFocus();

        InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getActivity()).getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchString.getWindowToken(), 0);

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        Uri.Builder builder = new Uri.Builder();
                builder.path("course")
                .appendPath("search-course")
                .appendPath(Objects.requireNonNull(searchString.getText()).toString());
        String myUrl = builder.build().toString();

        alertDialog.show();
        iMyService.getSearchCourse(myUrl)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        if (s.contains("vote")) {
                            courseItems = new ArrayList<CourseItem>();
                            try {
                                JSONArray ja = new JSONArray(s);

                                for (int i = 0; i < ja.length(); i++) {
                                    JSONObject jsonObject = ja.getJSONObject(i);

                                    CourseItem courseItem = new CourseItem();
                                    courseItem.setID(jsonObject.getString("_id"));
                                    courseItem.setTitle(jsonObject.getString("name"));
                                    courseItem.setUpdateTime(jsonObject.getString("created_at"));
                                    courseItem.setUrl(jsonObject.getString("image"));
                                    courseItem.setGoal(jsonObject.getString("goal"));
                                    courseItem.setDescription(jsonObject.getString("description"));
                                    courseItem.setCategoryID(jsonObject.getString("category"));
                                    courseItem.setPrice((float) jsonObject.getDouble("price"));

                                    courseItems.add(courseItem);
                                }

                                personalCourseAdapter = new PersonalCourseAdapter(courseItems);
                                personalCourseAdapter.notifyDataSetChanged();
                                searchResult.setAdapter(personalCourseAdapter);
                                searchResult.setLayoutManager(new LinearLayoutManager(getActivity(),
                                        LinearLayoutManager.VERTICAL, false));

                            } catch (JSONException jx) {
                                jx.printStackTrace();
                                Toast.makeText(getActivity(), "JSONEX", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);
                    }
                });
    }
}
