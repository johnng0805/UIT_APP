package com.example.uit_app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import Model.CourseItem;
import Model.RatingComment;
import Model.UserAccount;
import Retrofit.IMyService;
import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
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

    RatingBar ratingBar;
    CourseCommentAdapter courseCommentAdapter;
    ArrayList<RatingComment> ratingComments;

    EditText courseComment;
    boolean sentComment = false;
    CircleImageView userAvatar;

    Boolean joined = false;

    ArrayList<CourseItem> courseRelatedItem;
    CourseItemAdapter courseItemAdapter;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    JSONArray cartArray = new JSONArray();
    boolean checkCart = false;
    boolean checkJoined = false;

    private static String urlImg = "http://149.28.24.98:9000/upload/course_image/";
    private static String urlComment = "http://149.28.24.98:9000/rate/get-rate-by-course/";
    private static String urlJoinedCourse = "http://149.28.24.98:9000/course/check-is-bough-this-course/";
    private static String urlAvatar = "http://149.28.24.98:9000/upload/user_image/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        courseItem = (CourseItem) getIntent().getSerializableExtra("courseItem");

        setUIReference();

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        checkJoinedCourse();

        courseRelatedItem = new ArrayList<CourseItem>();
        loadRelatedCourse();

        ratingComments = new ArrayList<RatingComment>();
        getComment();

        try {
            cartArray = new JSONArray(sharedPreferences.getString("cartArray", ""));
            for (int i = 0; i < cartArray.length(); i++) {
                checkCart = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (courseItem.getPrice() == 0) {
                    joinCourse();
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("diffUser", false);
                    editor.apply();
                    addToCart();
                }
            }
        });

        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ratingBar.getRating() == 0 || courseComment.getText() == null) {
                    Toast.makeText(CourseDetailsActivity.this, "Please rate and comment", Toast.LENGTH_SHORT).show();
                } else {
                    postRating();

                    courseComment.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getApplicationContext())
                            .getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(courseComment.getWindowToken(), 0);
                }
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


    private void addToCart() {
        String stringFromJSONArray = cartArray.toString();
        if (stringFromJSONArray.contains(courseItem.getID())) {
            Toast.makeText(this, "Course is already in cart.", Toast.LENGTH_SHORT).show();
        } else {
            JSONObject jo = new JSONObject();
            try {
                jo.put("courseImage", courseItem.getUrl());
                jo.put("author", courseItem.getAuthor());
                jo.put("courseID", courseItem.getID());
                jo.put("title", courseItem.getTitle());
                jo.put("price", courseItem.getPrice());
                jo.put("discount", courseItem.getDiscount());
                jo.put("categoryID", courseItem.getCategoryID());

            } catch (JSONException jx) {
                jx.printStackTrace();
            }

            cartArray.put(jo);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("cartArray", cartArray.toString());
            editor.apply();
            Toast.makeText(this, "Added to cart.", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkJoinedCourse() {
        alertDialog.show();
        iMyService.checkJoinedCourse(urlJoinedCourse + courseItem.getID() + "/" +sharedPreferences.getString("id", ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        try {
                            JSONObject jo = new JSONObject(s);
                            checkJoined = jo.getBoolean("bought");
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

                        if (checkJoined) {
                            addBtn.setClickable(false);
                            addBtn.setFocusable(false);
                            addBtn.setText(R.string.joined_btn);
                        }
                    }
                });
    }

    private void getComment() {
        iMyService.getListComment(urlComment + courseItem.getID())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        try {
                            JSONArray ja = new JSONArray(s);

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                JSONObject joUser = jo.getJSONObject("idUser");

                                RatingComment ratingComment = new RatingComment();
                                ratingComment.setUserName(joUser.getString("name"));
                                ratingComment.setAvatar(joUser.getString("image"));
                                ratingComment.setCommentContent(jo.getString("content"));
                                ratingComment.setNumStar((float) jo.getDouble("numStar"));

                                ratingComments.add(ratingComment);
                            }

                            courseCommentAdapter = new CourseCommentAdapter(CourseDetailsActivity.this, ratingComments);
                            courseRating.setAdapter(courseCommentAdapter);
                            courseRating.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                                    LinearLayoutManager.VERTICAL, false));
                        } catch (JSONException jx) {
                            jx.printStackTrace();
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
                        Toast.makeText(CourseDetailsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void postRating() {
        float userStar = ratingBar.getRating();
        String userComment = courseComment.getText().toString().trim();

        JSONObject jo = new JSONObject();
        try {
            jo.put("idUser", sharedPreferences.getString("id", ""));
            jo.put("idCourse", courseItem.getID());
            jo.put("content", userComment);
            jo.put("numStar", userStar);
        } catch (JSONException jx) {
            jx.printStackTrace();
        }

        alertDialog.show();
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jo.toString());
        iMyService.postCommentRating(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        sentComment = true;
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
                        if (sentComment) {
                            Toast.makeText(CourseDetailsActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                            getComment();
                        } else {
                            Toast.makeText(CourseDetailsActivity.this, "Error!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void joinCourse() {

        addBtn.setClickable(false);
        addBtn.setFocusable(false);

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
                            Toast.makeText(CourseDetailsActivity.this, "You have already joined.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CourseDetailsActivity.this, "Joined Successfully.", Toast.LENGTH_SHORT).show();
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
        ratingBar = findViewById(R.id.rating_star);
        courseComment = findViewById(R.id.user_comment);
        userAvatar = findViewById(R.id.user_avatar);

        Picasso.get().load(urlImg+courseItem.getUrl())
                .placeholder(R.drawable.devices)
                .error(R.drawable.devices)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(courseImg);

        Picasso.get().load(urlAvatar + sharedPreferences.getString("image", ""))
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(userAvatar);

        courseTitle.setText(courseItem.getTitle());
        courseAuthor.setText(courseItem.getAuthor());

        NumberFormat priceFormat = new DecimalFormat("#,###");
        coursePrice.setText(priceFormat.format(courseItem.getPrice()));

        /*SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        courseDate.setText(simpleDateFormat.format(courseItem.getUpdateTime()));*/

        courseDate.setText(String.valueOf(courseItem.getUpdateTime()));
        courseOverview.setText(courseItem.getDescription());
        courseObjective.setText(courseItem.getGoal());
    }
}
