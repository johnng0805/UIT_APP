package com.example.uit_app;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

//import com.denzcoskun.imageslider.ImageSlider;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.uit_app.CategoryItemAdapter;
import com.example.uit_app.CourseItemAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

public class FeatureFragment extends Fragment {

    ImageSlider imageSlider;
    ArrayList<SlideModel> imageList;
    boolean flagCategory = false;

    //---Courses---
    ArrayList<CourseItem> courseLatest, courseFree, courseBest;
    CourseItemAdapter latestCourseAdapter, freeCourseAdapter, bestCourseAdapter;
    RecyclerView latestView, freeView, bestView;

    //---Categories---
    ArrayList<CategoryItem> categoryItems;
    CategoryItemAdapter categoryItemAdapter;
    RecyclerView categoriesView;

    //---Retrofit---
    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    public FeatureFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_feature, container, false);

        imageSlider = rootView.findViewById(R.id.image_slider);
        latestView = rootView.findViewById(R.id.latest_view);
        freeView = rootView.findViewById(R.id.free_view);
        bestView = rootView.findViewById(R.id.best_view);
        categoriesView = rootView.findViewById(R.id.categories_view);

        imageList = new ArrayList<SlideModel>();
        imageList.add(new SlideModel("https://images.unsplash.com/photo-1599016012665-13b74bb3b528?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1493&q=80", "Android", ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel("https://images.unsplash.com/photo-1593642532842-98d0fd5ebc1a?ixid=MXwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1950&q=80", "Study", ScaleTypes.CENTER_CROP));
        imageList.add(new SlideModel("https://images.unsplash.com/photo-1502945015378-0e284ca1a5be?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1350&q=80", "Work", ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(imageList, ScaleTypes.CENTER_CROP);
        imageSlider.setGravity(Gravity.CENTER);

        //Initialize Retrofit
        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(getContext()).build();

        //Load Courses
        categoryItems = new ArrayList<CategoryItem>();
        loadAllCategories();

        courseBest = new ArrayList<CourseItem>();
        loadBestCourses();

        courseFree = new ArrayList<CourseItem>();
        loadFreeCourses();

        courseLatest = new ArrayList<CourseItem>();
        loadLatestCourses();

        return rootView;
    }

    private void itemDebug(List<CategoryItem> items) {
        CategoryItem item = items.get(1);
    }

    private void loadAllCategories() {

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

                                categoryItemAdapter = new CategoryItemAdapter(getContext(), categoryItems);
                                categoriesView.setAdapter(categoryItemAdapter);
                                categoriesView.setLayoutManager(new LinearLayoutManager(getContext(),
                                        LinearLayoutManager.HORIZONTAL, false));

                            } catch (JSONException jx) {
                                jx.printStackTrace();
                            }
                        }
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
                    }
                });
    }

    private void loadFreeCourses() {

        iMyService.getFreeCourse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        if (s.contains("vote")) {
                            try {
                                JSONArray ja = new JSONArray(s);
                                int len = ja.length();

                                for (int i = 0; i < len; i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    JSONObject joSub = jo.getJSONObject("vote");
                                    JSONObject joCat = jo.getJSONObject("category");
                                    JSONObject joAuthor = jo.getJSONObject("idUser");

                                    CourseItem item = new CourseItem();
                                    item.setTitle(jo.getString("name"));
                                    item.setAuthorID(joAuthor.getString("_id"));
                                    item.setAuthor(joAuthor.getString("name"));
                                    item.setTotalVote(joSub.getInt("totalVote"));
                                    item.setDiscount(jo.getInt("discount"));
                                    item.setRanking(jo.getString("ranking"));
                                    item.setUpdateTime(jo.getString("created_at"));
                                    item.setID(jo.getString("_id"));
                                    item.setUrl(jo.getString("image"));
                                    item.setGoal(jo.getString("goal"));
                                    item.setDescription(jo.getString("description"));
                                    item.setCategoryName(joCat.getString("name"));
                                    item.setCategoryID(joCat.getString("_id"));
                                    item.setPrice(jo.getInt("price"));

                                    courseFree.add(item);
                                }

                                freeCourseAdapter = new CourseItemAdapter(getContext(), courseFree);
                                freeView.setAdapter(freeCourseAdapter);
                                freeView.setLayoutManager(new LinearLayoutManager(getContext(),
                                        LinearLayoutManager.HORIZONTAL, false));

                            } catch (JSONException jx) {
                                jx.printStackTrace();
                            }
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

    private void loadLatestCourses() {

        iMyService.getAllCourse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        if (s.contains("vote")) {
                            try {
                                JSONArray ja = new JSONArray(s);
                                int len = ja.length();

                                for (int i = 0; i < 12; i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    JSONObject joSub = jo.getJSONObject("vote");
                                    JSONObject joCat = jo.getJSONObject("category");
                                    JSONObject joAuthor = jo.getJSONObject("idUser");

                                    CourseItem item = new CourseItem();
                                    item.setTitle(jo.getString("name"));
                                    item.setAuthorID(joAuthor.getString("_id"));
                                    item.setAuthor(joAuthor.getString("name"));
                                    item.setTotalVote(joSub.getInt("totalVote"));
                                    item.setDiscount(jo.getInt("discount"));
                                    item.setRanking(jo.getString("ranking"));
                                    item.setUpdateTime(jo.getString("created_at"));
                                    item.setID(jo.getString("_id"));
                                    item.setUrl(jo.getString("image"));
                                    item.setGoal(jo.getString("goal"));
                                    item.setDescription(jo.getString("description"));
                                    item.setCategoryName(joCat.getString("name"));
                                    item.setCategoryID(joCat.getString("_id"));
                                    item.setPrice(jo.getInt("price"));

                                    courseLatest.add(item);
                                }

                                latestCourseAdapter = new CourseItemAdapter(getContext(), courseLatest);
                                latestView.setAdapter(latestCourseAdapter);
                                latestView.setLayoutManager(new LinearLayoutManager(getContext(),
                                        LinearLayoutManager.HORIZONTAL, false));
                            } catch (JSONException jx) {
                                jx.printStackTrace();
                            }
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

    private void loadBestCourses() {

        iMyService.getTopCourse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {
                        if (s.contains("vote")) {
                            try {
                                JSONArray ja = new JSONArray(s);
                                int len = ja.length();

                                for (int i = 0; i < len; i++) {
                                    JSONObject jo = ja.getJSONObject(i);
                                    JSONObject joSub = jo.getJSONObject("vote");
                                    JSONObject joCat = jo.getJSONObject("category");
                                    JSONObject joAuthor = jo.getJSONObject("idUser");

                                    CourseItem item = new CourseItem();
                                    item.setTitle(jo.getString("name"));
                                    item.setAuthor(joAuthor.getString("name"));
                                    item.setAuthorID(joAuthor.getString("_id"));
                                    item.setTotalVote(joSub.getInt("totalVote"));
                                    item.setDiscount(jo.getInt("discount"));
                                    item.setRanking(jo.getString("ranking"));
                                    item.setUpdateTime(jo.getString("created_at"));
                                    item.setID(jo.getString("_id"));
                                    item.setUrl(jo.getString("image"));
                                    item.setGoal(jo.getString("goal"));
                                    item.setDescription(jo.getString("description"));
                                    item.setCategoryName(joCat.getString("name"));
                                    item.setCategoryID(joCat.getString("_id"));
                                    item.setPrice(jo.getInt("price"));

                                    courseBest.add(item);
                                }

                                bestCourseAdapter = new CourseItemAdapter(getContext(), courseBest);
                                bestView.setAdapter(bestCourseAdapter);
                                bestView.setLayoutManager(new LinearLayoutManager(getContext(),
                                        LinearLayoutManager.HORIZONTAL, false));

                            } catch (JSONException jx) {
                                jx.printStackTrace();
                            }
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
}
