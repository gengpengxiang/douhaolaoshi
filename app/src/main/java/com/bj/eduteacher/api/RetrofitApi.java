package com.bj.eduteacher.api;

import com.bj.eduteacher.community.publish.model.UploadResult;
import com.bj.eduteacher.login.model.LoginInfo;
import com.bj.eduteacher.login.model.UserInfo;
import com.bj.eduteacher.prize.model.Prize;
import com.bj.eduteacher.userinfo.model.BinderInfo;
import com.bj.eduteacher.wxapi.WXToken;
import com.bj.eduteacher.wxapi.WXUserInfo;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

import static com.bj.eduteacher.api.Urls.GETTEACHERINFO;
import static com.bj.eduteacher.api.Urls.JIANGPIN;
import static com.bj.eduteacher.api.Urls.LOGIN;
import static com.bj.eduteacher.api.Urls.TIMG;
import static com.bj.eduteacher.api.Urls.WEIXINPHONE;

/**
 * Created by Administrator on 2018/5/10 0010.
 */

public interface RetrofitApi {
    //获取微信登录token
    @GET("https://api.weixin.qq.com/sns/oauth2/access_token")
    Observable<WXToken> getWXcode(@Query("appid")String appid,@Query("secret")String secret,@Query("code")String code,@Query("grant_type")String grant_type);
    //获取微信登录用户信息
    @GET("https://api.weixin.qq.com/sns/userinfo")
    Observable<WXUserInfo> getWXUserInfo(@Query("access_token")String access_token, @Query("openid")String openid);
    //获取奖品信息
    @POST(JIANGPIN)
    @FormUrlEncoded
    Observable<Prize> getPrizeInfo(@Field("usercode")String usercode);
    //手机号登录
    @POST(LOGIN)
    @FormUrlEncoded
    Observable<LoginInfo> getLoginInfo(@Field("appkey")String appkey, @Field("teacherphone")String teacherphone, @Field("yzm")String yzm);
    //获取用户信息
    @POST(GETTEACHERINFO)
    @FormUrlEncoded
    //Observable<UserInfo> getUserInfo(@Field("appkey")String appkey, @Field("teacherphone")String teacherphone,@Field("unionid")String unionid);
    Observable<UserInfo> getUserInfo(@Field("appkey")String appkey, @Field("teacherphone")String teacherphone,@Field("unionid")String unionid,@Field("type")String type);
    //上传图片
    @POST(TIMG)
    @Multipart
    //Observable<UploadResult> uploadPic(@Part("appkey") String appkey, @PartMap Map<String,RequestBody> params);
    Observable<UploadResult> uploadPic( @Part List<MultipartBody.Part> partList);
    //手机号绑定微信，或者微信绑定手机号
    @POST(WEIXINPHONE)
    @FormUrlEncoded
    Observable<BinderInfo> getBindInfo(@Field("appkey")String appkey, @Field("unionid")String unionid,@Field("phone")String phone, @Field("laiyuan")String laiyuan);
}
