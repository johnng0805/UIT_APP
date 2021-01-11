package com.example.uit_app;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Objects;

import Interface.OnItemClick;
import Model.CourseItem;
import Model.LessonComment;
import Model.LessonItem;
import Model.SnapHelperOneByOne;
import Model.UserAccount;
import Retrofit.IMyService;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Retrofit;
import Retrofit.*;
import retrofit2.http.Url;

public class LearnCourseActivity extends AppCompatActivity implements OnItemClick {

    CourseItem courseItem;
    ArrayList<LessonItem> lessonItems;

    ProgressBar courseProgress;
    TextView courseProgressNumber;

    MediaController mediaController;

    TabItem documents, comments;
    TabLayout tabLayout;

    LinearSnapHelper linearSnapHelper;

    VideoView courseVideo;
    RecyclerView lessonItemView, documentItemView;
    TextView lessonTitle;
    EditText lessonCommentET;
    Button commentSendButton;

    LessonItemAdapter lessonItemAdapter;
    DocumentItemAdapter documentItemAdapter;
    ArrayList<String> courseDocuments;

    ArrayList<LessonComment> lessonComments;
    LessonCommentAdapter lessonCommentAdapter;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;
    SharedPreferences sharedPreferences;

    int currentLessonPos, currentDoc;
    boolean sent = false;
    boolean lessonClicked = false;

    String urlGetLesson = "http://149.28.24.98:9000/lesson/get-lesson-by-id-course/";
    String urlGetProgrress = "http://149.28.24.98:9000/join/get-progress-course-join-by-idUser-and-idCourse/";
    String urlGetVideo = "http://149.28.24.98:9000/upload/lesson/";
    String urlGetParentComment = "http://149.28.24.98:9000/comment/get-parent-comment-by-lesson/";

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
        lessonComments = new ArrayList<LessonComment>();
        getLesson();
        getProgress();

        commentSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!lessonClicked) {
                    Toast.makeText(LearnCourseActivity.this, "Please select lesson before commenting", Toast.LENGTH_SHORT).show();
                } else if (lessonCommentET.getText().length() == 0) {
                    Toast.makeText(LearnCourseActivity.this, "Please write a comment", Toast.LENGTH_SHORT).show();
                } else {
                    sendComment();
                    lessonCommentET.clearFocus();
                    InputMethodManager inputMethodManager = (InputMethodManager) Objects.requireNonNull(getApplicationContext())
                            .getSystemService(INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(lessonCommentET.getWindowToken(), 0);
                }
            }
        });
    }

    private void setUIReference() {
        courseVideo = findViewById(R.id.coursevideo);
        lessonItemView = findViewById(R.id.lesson_item_view);
        documentItemView = findViewById(R.id.learning_items);
        courseProgress = findViewById(R.id.progress_bar);
        courseProgressNumber = findViewById(R.id.progressnumber);
        documents = findViewById(R.id.documents);
        comments = findViewById(R.id.comments);
        tabLayout = findViewById(R.id.tablayout);
        lessonTitle = findViewById(R.id.course_title);
        lessonCommentET = findViewById(R.id.comment_box);
        commentSendButton = findViewById(R.id.comment_sendBtn);
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
                                    LinearLayoutManager.HORIZONTAL, false));

                            linearSnapHelper = new SnapHelperOneByOne();
                            linearSnapHelper.attachToRecyclerView(lessonItemView);

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

    private void getParentComment(int position) {

        iMyService.getParentComment(urlGetParentComment + courseItem.getID() + "/"
                + lessonItems.get(position).getID() + "/"
                + "8" + "/" + "0")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        //debugFunc(s);
                        try {
                            JSONArray ja = new JSONArray(s);

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject jo = ja.getJSONObject(i);
                                JSONObject joUser = jo.getJSONObject("idUser");

                                LessonComment lessonComment = new LessonComment();
                                lessonComment.setID(jo.getString("_id"));
                                lessonComment.setContent(jo.getString("content"));
                                lessonComment.setCreatedAt(jo.getString("created_at"));
                                lessonComment.setIdCourse(jo.getString("idCourse"));
                                lessonComment.setIdLesson(jo.getString("idLesson"));
                                lessonComment.setIdParent(jo.getString("idParent"));
                                lessonComment.setImage(jo.getString("image"));
                                lessonComment.setPageType(jo.getString("pageType"));

                                UserAccount userAccount = new UserAccount();
                                userAccount.setHoten(joUser.getString("name"));
                                userAccount.setMail(joUser.getString("email"));
                                userAccount.setAva(joUser.getString("image"));
                                userAccount.setID(joUser.getString("_id"));

                                lessonComment.setIdUser(userAccount);

                                lessonComments.add(lessonComment);
                            }

                            lessonCommentAdapter = new LessonCommentAdapter(LearnCourseActivity.this, lessonComments);
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
    public void onItemClick(String type, int position) {
        if (type.equals("lesson")) {
            courseDocuments = new ArrayList<String>();
            courseDocuments = lessonItems.get(position).getDocuments();

            lessonTitle.setText(lessonItems.get(position).getTitle());
            currentLessonPos = position;
            lessonClicked = true;

            switch (tabLayout.getSelectedTabPosition()) {
                case 0:
                    documentItemAdapter = new DocumentItemAdapter(LearnCourseActivity.this,
                            courseDocuments, LearnCourseActivity.this);
                    documentItemView.setAdapter(documentItemAdapter);
                    documentItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                            LinearLayoutManager.VERTICAL, false));
                    break;
                case 1:
                    getParentComment(position);
                    documentItemView.setAdapter(lessonCommentAdapter);
                    documentItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                            LinearLayoutManager.VERTICAL, false));
                    break;
            }

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            documentItemAdapter = new DocumentItemAdapter(LearnCourseActivity.this,
                                    courseDocuments, LearnCourseActivity.this);
                            documentItemView.setAdapter(documentItemAdapter);
                            documentItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                                    LinearLayoutManager.VERTICAL, false));
                            break;
                        case 1:
                            getParentComment(position);
                            documentItemView.setAdapter(lessonCommentAdapter);
                            documentItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                                    LinearLayoutManager.VERTICAL, false));
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    switch (tab.getPosition()) {
                        case 0:
                            documentItemAdapter = new DocumentItemAdapter(LearnCourseActivity.this,
                                    courseDocuments, LearnCourseActivity.this);
                            documentItemView.setAdapter(documentItemAdapter);
                            documentItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                                    LinearLayoutManager.VERTICAL, false));
                            break;
                        case 1:
                            getParentComment(position);
                            documentItemView.setAdapter(lessonCommentAdapter);
                            documentItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                                    LinearLayoutManager.VERTICAL, false));
                            break;
                    }
                }
            });

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
        } else if (type.equals("doc")) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permissions, 100);
            } else {
                currentDoc = position;
                try {
                    startDownloading(position);
                } catch (URISyntaxException ux) {
                    ux.printStackTrace();
                }
            }
        }
    }

    private void sendComment() {

        alertDialog.show();

        JSONObject jo = new JSONObject();
        try {
            jo.put("idParent", null);
            jo.put("idCourse", courseItem.getID());
            jo.put("content", lessonCommentET.getText().toString().trim());
            jo.put("idUser", sharedPreferences.getString("id", ""));
            jo.put("idLesson", lessonItems.get(currentLessonPos).getID());
            jo.put("image", "");
        } catch (JSONException jx) {
            jx.printStackTrace();
        }

        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jo.toString());
        iMyService.postCommentLesson(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull String s) {
                        if (s.contains("image")) {
                            sent = true;
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

                        if (currentLessonPos == 1) {
                            getParentComment(currentLessonPos);
                            lessonCommentAdapter = new LessonCommentAdapter(LearnCourseActivity.this, lessonComments);
                            documentItemView.setAdapter(lessonCommentAdapter);
                            documentItemView.setLayoutManager(new LinearLayoutManager(LearnCourseActivity.this,
                                    LinearLayoutManager.VERTICAL, false));
                        }

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);

                        if (sent) {
                            Toast.makeText(LearnCourseActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                            lessonCommentET.getText().clear();
                        } else {
                            Toast.makeText(LearnCourseActivity.this, "Cannot send comment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @androidx.annotation.NonNull String[] permissions, @androidx.annotation.NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        startDownloading(currentDoc);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void startDownloading(int position) throws URISyntaxException {
        String docName = lessonItems.get(currentLessonPos).getDocuments().get(currentDoc);
        int extPos = docName.indexOf(".");
        Uri uri = Uri.parse(urlGetVideo + docName);

        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);

        request.setTitle(docName);
        request.setDescription("Downloading file...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOCUMENTS, ""+ docName.substring(0, extPos));

        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }
}
