package Retrofit;

import androidx.annotation.NonNull;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface IMyService {
    @POST("login")
    @FormUrlEncoded
    Observable<Response<String>> loginUser(@Field("email") String email, @Field("password") String actToken);

    @POST("register")
    @FormUrlEncoded
    Observable<Response<String>> registerUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("address") String address,
            @Field("description") String description,
            @Field("gender") String gender);

    //Active Account Activity
    @POST("active-account")
    @FormUrlEncoded
    Observable<String> activeAccUser(@Field("email") String email,
                                     @Field("activeToken") String actToken);

    //UserInfo Activity
    @PUT("change-profile")
    @FormUrlEncoded
    Observable<Response<String>> changeProfile(@Field("name") String oldPass,
                                               @Field("phone") String phone,
                                               @Field("address") String address,
                                               @Field("description") String description,
                                               @Field("gender") String gender,
                                               @Header("auth-token") String authToken);

    @PUT("change-password")
    @FormUrlEncoded
    Observable<Response<String>> changePassword(@Field("oldpassword") String oldPass,
                                                @Field("newpassword") String newPass,
                                                @Header("auth-token") String authToken);

    @Multipart
    @PUT("change-avatar")
    Observable<Response<String>>  changeAva(@Part MultipartBody.Part file,
                                            @Header("auth-token") String authToken);

    //Account Fragment
    @GET("logout")
    Observable<String>  userLogout(@Header("auth-token") String authToken);

    //FeatureFragment - Week 3
    @GET("category/get-all-category")
    Observable<String>  getAllCategory();

    @GET("course/get-all")
    Observable<String>  getAllCourse();

    @GET("course/get-free")
    Observable<String>  getFreeCourse();

    @GET("course/get-top")
    Observable<String>  getTopCourse();

    @GET
    Observable<String> getCourseByID(@Url String urlGet);

    @GET()
    Observable<String> getCategoryByID(@Url String urlGet);

    //Joined Course
    @GET
    Observable<String> checkJoinedCourse(@Url String urlGet);

    @GET
    Observable<String> getJoinedCourse(@Url String urlGet);

    @POST("join/create-join")
    @FormUrlEncoded
    Observable<Response<String>> joinCourse(@Field("idUser") String name,
                                            @Field("idCourse") String course);

    //Course Detail
    @GET
    Observable<String> getListComment(@Url String urlGet);

    @POST("rate/create-rate")
    Observable<String> postCommentRating(@Body RequestBody body);

    //Search
    @GET
    Observable<String> getSearchCourse(@Url String urlGet);

    @GET
    Observable<String> getCourseByCategory(@Url String urlGet);

    @POST("payment/pay")
    Observable<String> pay(@Body RequestBody body);

    //Forgot Password Email
    @POST("forgot-password")
    @FormUrlEncoded
    Observable<String> forgotPassword(@Field("email") String email);

    //Reset Password
    @POST("reset-password")
    @FormUrlEncoded
    Observable<String> resetPassword(@Field("email") String email,
                                     @Field("password") String password,
                                     @Field("token") String token);

    @Multipart
    @POST("/course/create")
    Observable<Response<String>> createCourse(@Header("auth-token") String authToken,
                                              @Part("name") String name,
                                              @Part("goal") String goal,
                                              @Part("category") String category,
                                              @Part("description") String description,
                                              @Part("price") String price,
                                              @Part("discount") String discount,
                                              @Part MultipartBody.Part file);

    @GET
    Observable<String> getCreatedCourse(@Url String urlGet);

    //Learn Course
    @GET
    Observable<String> getLesson(@Header("auth-token") String authToken,
                                 @Url String urlGet);

    @GET
    Observable<String> getProgress(@Url String urlGet);

    @GET
    Observable<String> getParentComment(@Url String urlGet);

    @DELETE
    Observable<String>  deleteCourse(@Url String urlGet, @Header("auth-token") String authToken);

    @POST("comment/add-comment")
    Observable<String> postCommentLesson(@Body RequestBody body);

    @POST("join/update-progress-lesson-of-course/")
    Observable<Response<String>> updateProgress(@Url String urlGet, @Body RequestBody body);
}
