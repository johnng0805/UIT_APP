package com.example.uit_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.*;
import dmax.dialog.SpotsDialog;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;



public class AccountAvatarActivity extends AppCompatActivity {
    Button galleryBtn, cameraBtn, saveBtn;
    ImageView avatar;
    Uri imageUri;
    Bitmap bitmap;
    UserAccount userAccount;
    File file;

    boolean flag = false;

    IMyService iMyService;
    Retrofit retrofit;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_avatar);
        setUIReference();
        userAccount = (UserAccount) getIntent().getSerializableExtra("userAcc");

        alertDialog = new SpotsDialog.Builder().setContext(this).build();
        retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);

        String url = "http://149.28.24.98:9000/upload/user_image/";
        Picasso.get().load(url+userAccount.getAva()).placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(avatar);

        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 100);
                } else {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 101);
                }
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, 1000);
                } else {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 1001);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeAvatar();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 100: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, 101);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

                break;
            }
            case 1000: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(galleryIntent, 1001);
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }

                break;
            }
        }
    }

    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    private String getPath(Uri imageUri) {
        String path = "";
        if (getContentResolver() != null) {
            Cursor cursor = getContentResolver().query(imageUri, null, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                path = cursor.getString(index);
                cursor.close();
            }
        }
        return path;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1001: {
                if (resultCode == RESULT_OK && data != null) {
                    imageUri = data.getData();
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        file = new File(getPath(imageUri));
                        avatar.setImageBitmap(bitmap);
                        userAccount.setAva(file.getName());
                        break;
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            case 101: {
                if (resultCode == RESULT_OK & data != null) {
                    bitmap = (Bitmap) data.getExtras().get("data");
                    Uri imageUri = getImageUri(getApplicationContext(), bitmap);
                    file = new File(getPath(imageUri));
                    avatar.setImageBitmap(bitmap);
                    userAccount.setAva(file.getName());
                    break;
                }
            }
        }
    }

    private static String between(String start, String end, String input) {
        int startIndex = input.indexOf(start);
        int endIndex = input.indexOf(end);
        if (startIndex == -1 || endIndex == -1)
            return input;
        else
            return input.substring(startIndex + start.length(), endIndex + end.length());
    }

    private void changeAvatar() {
        saveBtn.setClickable(false);
        saveBtn.setFocusable(false);

        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpg"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), requestBody);
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");

        alertDialog.show();
        iMyService.changeAva(part, userAccount.getToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Response<String> stringResponse) {
                        if (stringResponse.isSuccessful()) {
                            if (stringResponse.message().equals("OK")) {
                                String response = stringResponse.body().toString();
                                String start = "\"image\"";
                                String end = "\"gender\"";
                                String avaName = between(start, end, response);
                                String[] arg = avaName.split("\"");
                                userAccount.setAva(arg[1]);
                                flag = true;
                            }

                        } else {
                            flag = false;
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
                        Toast.makeText(AccountAvatarActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        saveBtn.setClickable(true);
                        saveBtn.setFocusable(true);
                    }

                    @Override
                    public void onComplete() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                alertDialog.dismiss();
                            }
                        }, 500);

                        if (flag) {
                            Toast.makeText(AccountAvatarActivity.this, "Avatar Update Successful", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent();
                            intent.putExtra("userAcc", userAccount);

                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else {
                            Toast.makeText(AccountAvatarActivity.this, "Avatar Update Failed", Toast.LENGTH_SHORT).show();
                        }

                        saveBtn.setClickable(true);
                        saveBtn.setFocusable(true);
                    }
                });
    }

    private void setUIReference() {
        galleryBtn = findViewById(R.id.gallery_btn);
        cameraBtn = findViewById(R.id.camera_btn);
        saveBtn = findViewById(R.id.avatar_save_button);
        avatar = findViewById(R.id.imgview_avatar);
    }
}
