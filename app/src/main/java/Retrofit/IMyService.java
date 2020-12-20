package Retrofit;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.http.Body;
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

    //Joined Course
    @GET
    Observable<String> getJoinedCourse(@Url String urlGet);

    @POST("join/create-join")
    @FormUrlEncoded
    Observable<Response<String>> joinCourse(@Field("idUser") String name,
                                            @Field("idCourse") String course);

    //Course Detail
    @GET
    Observable<String> getListComment(@Url String urlGet);

    //Search
    @GET
    Observable<String> getSearchCourse(@Url String urlGet);

    @GET
    Observable<String> getCourseByCategory(@Url String urlGet);

    @POST("payment/pay")
    Observable<String> pay(@Body RequestBody body);
}
