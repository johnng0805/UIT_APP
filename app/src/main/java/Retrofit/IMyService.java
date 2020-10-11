package Retrofit;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface IMyService {
    @POST("login")
    @FormUrlEncoded
    Observable<Response<String>> loginUser(@Field("email") String email, @Field("password") String actToken);

    @POST("http://149.28.24.98:9000/register")
    @FormUrlEncoded
    Observable<Response<String>> registerUser(
            @Field("name") String name,
            @Field("email") String email,
            @Field("password") String password,
            @Field("phone") String phone,
            @Field("address") String address,
            @Field("description") String description,
            @Field("gender") String gender);
}
