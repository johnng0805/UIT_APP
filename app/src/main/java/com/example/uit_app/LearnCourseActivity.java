package com.example.uit_app;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import Interface.OnItemClick;
import Model.CourseItem;
import Model.LessonItem;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import Retrofit.*;
import retrofit2.http.Url;

public class LearnCourseActivity extends AppCompatActivity implements OnItemClick {

    CourseItem courseItem;
    ArrayList<LessonItem> lessonItems;

    ProgressBar courseProgress;
    TextView courseProgressNumber;

    MediaController mediaController;

    VideoView courseVideo;
    RecyclerView lessonItemView, documentItemView;
    LessonItemAdapter lessonItemAdapter;
    DocumentItemAdapter documentItemAdapter;
    ArrayList<String> courseDocuments;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;

    String urlGetLesson = "http://149.28.24.98:9000/lesson/get-lesson-by-id-course/";
    String urlGetProgrress = "http://149.28.24.98:9000/join/get-progress-course-join-by-idUser-and-idCourse/";
    String urlGetVideo = "http://149.28.24.98:9000/upload/lesson/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learning_course);

        courseItem = (CourseItem) getIntent().getSerializableExtra("courseItem");
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        setUIReference();

        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        alertDialog = new SpotsDialog.Builder().setContext(LearnCourseActivity.this).build();

        lessonItems = new ArrayList<LessonItem>();
        getLesson();
        getProgress();
    }

    private void setUIReference() {
        courseVideo = findViewById(R.id.coursevideo);
        lessonItemView = findViewById(R.id.lesson_item_view);
        documentItemView = findViewById(R.id.learning_items);
        courseProgress = findViewById(R.id.progress_bar);
        courseProgressNumber = findViewById(R.id.progressnumber);
    }

    private void debugFunc(String s) {
        String temp = s;
    }

    private void getLesson() {

        alertDialog.show();
        iMyService.getLesson(sharedPreferences.getString("token", ""), urlGetLesson + courseItem.getID())
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
                                JSONArray jaPop = jo.getJSONArray("popupQuestion");
                                JSONArray jaDoc = jo.getJSONArray("doc");

                                LessonItem item = new LessonItem();
                                item.setID(jo.getString("_id"));
                                item.setTitle(jo.getString("title"));
                                item.setOrder(jo.getInt("order"));
                                item.setCompleted(jo.getBoolean("isCompleted"));
                                item.setIDCourse(jo.getString("idCourse"));
                                item.setVideo(jo.getString("video"));

                                ArrayList<String> docTemp = new ArrayList<String>();
                                for (int j = 0; j < jaDoc.length(); j++) {
                                    docTemp.add(jaDoc.getString(j));
                                }

                                item.setDocuments(docTemp);

                                lessonItems.add(item);
                            }

                            lessonItemAdapter = new LessonItemAdapter(LearnCourseActivity.this, lessonItems, LearnCourseActivity.this);
                            lessonItemView.setAdapter(lessonItemAdapter);
                            lessonItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                                    LinearLayoutManager.VERTICAL, false));
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(LearnCourseActivity.this, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
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
                        Toast.makeText(LearnCourseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getProgress() {
        iMyService.getProgress(urlGetProgrress + sharedPreferences.getString("id", "")
                + "/" + courseItem.getID())
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
                            courseProgress.setMin(0);
                            courseProgress.setMax(100);
                            courseProgress.setProgress(jo.getInt("percentCompleted"), true);
                            courseProgressNumber.setText(String.valueOf(jo.getInt("percentCompleted")));
                        } catch (JSONException jx) {
                            jx.printStackTrace();
                            Toast.makeText(LearnCourseActivity.this, jx.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(LearnCourseActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void onItemClick(int position) {
        courseDocuments = new ArrayList<String>();
        courseDocuments = lessonItems.get(position).getDocuments();

        documentItemAdapter = new DocumentItemAdapter(LearnCourseActivity.this, courseDocuments);
        documentItemView.setAdapter(documentItemAdapter);
        documentItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                RecyclerView.VERTICAL, false));

        Uri videoUrl = Uri.parse("http://149.28.24.98:9000/upload/lesson/" + lessonItems.get(position).getVideo());
        mediaController = new MediaController(LearnCourseActivity.this);
        mediaController.setAnchorView(courseVideo);

        courseVideo.setMediaController(mediaController);
        courseVideo.setVideoURI(videoUrl);

        courseVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                courseVideo.start();
            }
        });

        courseVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
    }
}
