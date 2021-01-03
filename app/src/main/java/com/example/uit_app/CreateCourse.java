package com.example.uit_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import es.dmoral.toasty.Toasty;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import Retrofit.IMyService;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


import dmax.dialog.SpotsDialog;
import retrofit2.Retrofit;
import Retrofit.RetrofitClient;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;


public class CreateCourse extends AppCompatActivity {

    EditText courseName, courseTarget, courseDescription, coursePrice, courseDiscount;
    Spinner spinner;
    ImageView courseImage;
    Button galleryBtn, completeBtn;
    TextView category;

    String token, name, target, description, price, discount, idCategory;

    Bitmap bitmap;
    File file;
    IMyService iMyService;
    SharedPreferences sharedPreferences;
    AlertDialog alertDialog;

    boolean flag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(CreateCourse.this);
        token =  sharedPreferences.getString("token", "");
        alertDialog = new SpotsDialog.Builder().setContext(this).build();

        setUIReference();

        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService = retrofit.create(IMyService.class);
        spinnerActivity();

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_DENIED){
                        //permission not granted, request it.
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show popup for runtime permission
                        requestPermissions(permissions, 1000);
                    }
                    else {
                        //permission already granted
                        pickImageFromGallery();
                    }
                }
                else {
                    //system os is less then marshmallow
                    pickImageFromGallery();
                }

            }
        });

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCourses();
            }
        });


    }


    private void spinnerActivity(){
        category = findViewById(R.id.create_course_category);

        // 3 dòng tiêu chuẩn cho code spinner adapter
        // 2 loại layout tiêu chẩn của android. dòng trên xet tạo thành spinner, dòng dưới set spinner có thể chọn chức năng theo drop down sp
        /* link tra: https://developer.android.com/guide/topics/ui/controls/spinner */

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = findViewById(R.id.category_spinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String categoriesTitle = parent.getItemAtPosition(position).toString();

                if (categoriesTitle.equals("Toán Tin")){
                    category.setText("Toán Tin");
                    idCategory = "5f66f8bc877b74b5e133db8c";
                }
                else if (categoriesTitle.startsWith("Công nghệ thông tin")){
                    category.setText("Công nghệ thông tin");
                    idCategory = "5f66f8e0877b74b5e133db8d";
                }

                else if (categoriesTitle.equals("Ngoại ngữ")) {
                    category.setText("Ngoại ngữ");
                    idCategory = "5fa4ac6fb4e3807bf40fed22";
                }
                else return;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    // activity cập nhật ảnh.
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == 1000 && data.getData() != null) {
            //set image to image view

            Uri path = data.getData();
            try {

                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                courseImage.setImageBitmap(bitmap);
                file = new File(getRealPathFromURI(path));
                flag = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1000:{


                if (grantResults.length >0 && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED){
                    //permission was granted
                    pickImageFromGallery();
                }
                else {
                    //permission was denied
                    Toast.makeText(this, "Permission denied...!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 1000);
    }


//    @NotNull
//    private MultipartBody.Part RequestImage(MultipartBody.Part p){
//        RequestBody fileReqBody =
//                RequestBody.create(
//                        MediaType.parse("image/*"),
//                        file
//                );
//        p = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
//        RequestBody destReqBody = RequestBody.create(MediaType.parse("text/plain"), "image-type");
//
//        return p;
//    }

    private void createCourses(){
        setButtonState(false);
        RequestBody fileReqBody =
                RequestBody.create(
                        MediaType.parse("image/jpg"),
                        file
                );
        MultipartBody.Part part = MultipartBody.Part.createFormData("image", file.getName(), fileReqBody);
//        RequestBody destReqBody = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        getString();

        alertDialog.show();
        iMyService.createCourse(token, name, target, idCategory, description, price, discount, part)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Response<String>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull Response<String> stringResponse) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        Toasty.error(CreateCourse.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {
                        finish();
                        Toasty.success(CreateCourse.this, "Thành công", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateCourse.this, MyCreatedCourse.class);
                        startActivity(intent);
                    }
                });
    }




    private void setUIReference() {

        courseName = findViewById(R.id.create_course_name_input);
        courseTarget = findViewById(R.id.create_course_target_input);
        courseDescription = findViewById(R.id.course_description_input);
        coursePrice = findViewById(R.id.create_course_price_input);
        courseDiscount = findViewById(R.id.create_course_discount_input);
        courseImage = findViewById(R.id.course_picture);
        spinner = findViewById(R.id.category_spinner);
        galleryBtn = findViewById(R.id.create_course_library);
        completeBtn = findViewById(R.id.submit_course_library);
    }

    private void getString(){
        name = courseName.getText().toString();
        target = courseTarget.getText().toString();
        description = courseDescription.getText().toString();
        price = coursePrice.getText().toString();
        discount = courseDiscount.getText().toString();
    }

    private void setButtonState (boolean state) {
        completeBtn.setClickable(state);
        completeBtn.setEnabled(state);
        galleryBtn.setClickable(state);
        galleryBtn.setEnabled(state);
    }
}