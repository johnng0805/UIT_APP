package com.example.uit_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Model.CategoryItem;
import Model.CourseItem;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;

public class CategoryItemActivity extends AppCompatActivity {

    TextView categoryTitle;
    RecyclerView categoryItemView;
    CategoryItem categoryItem;
    CategoryItemElementAdapter categoryItemElementAdapter;
    ArrayList<CourseItem> courseItems;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    String debug;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_item);

        //Set UI Reference
        categoryTitle = findViewById(R.id.category_item_title);
        categoryItemView = findViewById(R.id.category_item_view);
        courseItems = new ArrayList<CourseItem>();

        //Get category
        categoryItem = (CategoryItem) getIntent().getSerializableExtra("categoryItem");
        categoryTitle.setText(categoryItem.getName());

        loadCategoryElement();
    }

    private void loadCategoryElement() {
        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(CategoryItemActivity.this).build();
        alertDialog.show();
        iMyService.getCategoryByID("http://149.28.24.98:9000/course/getby-category/" + categoryItem.getID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        debug = s;
                        try {
                            JSONArray ja = new JSONArray(s);
                            int len = Math.min(ja.length(), 9);

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

                                courseItems.add(item);
                            }

                            categoryItemElementAdapter = new CategoryItemElementAdapter(CategoryItemActivity.this, courseItems);
                            categoryItemView.setAdapter(categoryItemElementAdapter);
                            categoryItemView.setLayoutManager(new LinearLayoutManager(CategoryItemActivity.this,
                                    LinearLayoutManager.VERTICAL, false));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);
                        Toast.makeText(CategoryItemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);

                        //Toast.makeText(CategoryItemActivity.this, debug, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}