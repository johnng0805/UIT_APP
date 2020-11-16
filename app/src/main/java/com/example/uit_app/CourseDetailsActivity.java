package com.example.uit_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

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
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;
import Retrofit.*;

public class CourseDetailsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    UserAccount userAccount;
    CourseItem courseItem;

    TextView courseTitle, courseAuthor, courseDate, coursePrice, courseObjective, courseOverview;
    ImageView courseImg;
    RecyclerView courseRelated, courseRating;
    Button addBtn, rateBtn;

    Boolean joined = false;

    ArrayList<CourseItem> courseRelatedItem;
    CourseItemAdapter courseItemAdapter;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    private static String urlImg = "http://149.28.24.98:9000/upload/course_image/";
    private static String urlComment = "http://149.28.24.98:9000/comment/get-parent-comment-by-lesson/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        courseItem = (CourseItem) getIntent().getSerializableExtra("courseItem");

        setUIReference();

        courseRelatedItem = new ArrayList<CourseItem>();
        loadRelatedCourse();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinCourse();
            }
        });
    }

    private void loadRelatedCourse() {
        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        iMyService.getTopCourse()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("vote")) {
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

                                        courseRelatedItem.add(item);
                                    }

                                    courseItemAdapter = new CourseItemAdapter(getApplicationContext(), courseRelatedItem);
                                    courseRelated.setAdapter(courseItemAdapter);
                                    courseRelated.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                                            LinearLayoutManager.HORIZONTAL, false));

                                } catch (JSONException jx) {
                                    jx.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(CourseDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void debugFun(String response) {
        String temp = response;
        String userID = sharedPreferences.getString("id", "");
    }

    private void addToCart() {

    }

    private void joinCourse() {

        addBtn.setClickable(false);
        addBtn.setFocusable(false);

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        alertDialog.show();
        iMyService.joinCourse(sharedPreferences.getString("id", ""), courseItem.getID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Response<String> stringResponse) {
                        if (stringResponse.isSuccessful()) {
                            joined = true;
                        }
                        debugFun(stringResponse.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);
                        Toast.makeText(CourseDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);

                        if (!joined) {
                            Toast.makeText(CourseDetailsActivity.this, "Cannot join course.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CourseDetailsActivity.this, "Joined Successfully", Toast.LENGTH_SHORT).show();
                        }

                        addBtn.setClickable(true);
                        addBtn.setFocusable(true);
                    }
                });
    }

    private void setUIReference() {
        courseTitle = findViewById(R.id.cd_title);
        courseAuthor = findViewById(R.id.by_author);
        courseDate = findViewById(R.id.from_time);
        coursePrice = findViewById(R.id.price_text);
        courseObjective = findViewById(R.id.objtext);
        courseOverview = findViewById(R.id.ovtext);
        courseImg = findViewById(R.id.imgview_cover);
        addBtn = findViewById(R.id.btnjoin);
        rateBtn = findViewById(R.id.btnrate);
        courseRelated = findViewById(R.id.related_course_view);
        courseRating = findViewById(R.id.user_rating_view);

        Picasso.get().load(urlImg+courseItem.getUrl())
                .placeholder(R.drawable.devices)
                .error(R.drawable.devices)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(courseImg);

        courseTitle.setText(courseItem.getTitle());
        courseAuthor.setText(courseItem.getAuthor());
        coursePrice.setText(String.valueOf(courseItem.getPrice()));
        courseDate.setText(String.valueOf(courseItem.getUpdateTime()));
        courseOverview.setText(courseItem.getDescription());
        courseObjective.setText(courseItem.getGoal());
    }
}
