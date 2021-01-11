package com.example.uit_app;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import Model.CourseItem;
import Model.UserAccount;
import Retrofit.IMyService;
import Retrofit.RetrofitClient;
import dmax.dialog.SpotsDialog;
import es.dmoral.toasty.Toasty;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CreatedCourseAdapter extends RecyclerView.Adapter<CreatedCourseAdapter.MyViewHolder> {

    private List<CourseItem> courseItems;
    private Context context;
    private static String url = "http://149.28.24.98:9000/course/delete/";

    IMyService iMyService;
    UserAccount userAccount;
    AlertDialog alertDialog;

    SharedPreferences sharedPreferences;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView courseImg, courseDelete;
        public TextView courseName, coursePercent;
        public MyViewHolder(View view) {
            super(view);
            courseName = (TextView) view.findViewById(R.id.item_created_course_name);
            coursePercent = (TextView) view.findViewById(R.id.item_created_percent);
            courseImg = (ImageView) view.findViewById(R.id.item_created_course_img);
            courseDelete = (ImageView) view.findViewById(R.id.delete_course);
        }
    }

    public CreatedCourseAdapter(List<CourseItem> courseItems) {
        this.courseItems = courseItems;
    }

    @NonNull
    @Override
    public CreatedCourseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View personalCourseView = inflater.inflate(R.layout.item_my_created_course, parent, false);
        return new MyViewHolder(personalCourseView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CourseItem item = courseItems.get(position);

        TextView courseName = holder.courseName;
        TextView coursePercent = holder.coursePercent;
        ImageView courseImg = holder.courseImg;
        ImageView courseDelete = holder.courseDelete;

        courseName.setText(item.getTitle());
        NumberFormat formatPrice = new DecimalFormat("#,###");
        NumberFormat formatPercent = new DecimalFormat("#%");

        coursePercent.setText(formatPercent.format(item.getPercent()));

        Picasso.get().load(url+item.getUrl())
                .placeholder(R.drawable.devices)
                .error(R.drawable.devices)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(courseImg);

        Picasso.get().load(item.getUrl())
                .placeholder(R.drawable.ic_baseline_delete_forever_24)
                .error(R.drawable.ic_baseline_delete_forever_24)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(courseDelete);



        courseDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RemoveCreatedCourse(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseItems.size();
    }

    private void RemoveCreatedCourse(int position) {

        courseItems.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, courseItems.size());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString("token","");
        String id = courseItems.get(position).getID();

        alertDialog = new SpotsDialog.Builder().setContext(context).build();
        Retrofit retrofit = RetrofitClient.getInstance();
        iMyService =  retrofit.create(IMyService.class);

        iMyService.deleteCourse("http://149.28.24.98:9000/course/delete/" + id, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onNext(@io.reactivex.annotations.NonNull String s) {

                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        new android.os.Handler().postDelayed(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        alertDialog.dismiss();
                                    }
                                },
                                500
                        );
                    }

                    @Override
                    public void onComplete() {
                        Toasty.success(context, "Xoá khoá học thành công", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(context, MyCreatedCourse.class);
//                        context.startActivity(intent);

                    }
                });
    }
}
