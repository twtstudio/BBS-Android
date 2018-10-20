package com.twtstudio.bbs.bdpqchen.bbs.commons.rx;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.twtstudio.bbs.bdpqchen.bbs.commons.App;
import com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants;
import com.twtstudio.bbs.bdpqchen.bbs.commons.tools.FileTool;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.LogUtil;
import com.twtstudio.bbs.bdpqchen.bbs.commons.utils.PrefUtil;
import com.twtstudio.bbs.bdpqchen.bbs.main.event.EventBean;
import com.twtstudio.bbs.bdpqchen.bbs.main.mainV3.BannerBean;

import java.io.File;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import static com.twtstudio.bbs.bdpqchen.bbs.commons.support.Constants.NET_RETROFIT_HEADER_TITLE;

public class RxDoHttpClientTest {

    private static final String BASE_HOST = Constants.BASE_HOST;
    private static final String BASE = "https://" + BASE_HOST;
    private static final String BASE_URL = BASE + "/testapi/";
    private BaseApi mApi;
    private static RxDoHttpClientTest sINSTANCE;

    private RxDoHttpClientTest() {
        Interceptor tokenInterceptor = chain -> {
            Request originalRequest = chain.request();
            Request authorised = originalRequest.newBuilder()
                    .header(NET_RETROFIT_HEADER_TITLE, getLatestAuthentication())
                    .build();
            return chain.proceed(authorised);
        };
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .cache(getCache())
                .addNetworkInterceptor(getNetWorkInterceptor())
                .addInterceptor(interceptor)
                .addInterceptor(tokenInterceptor)
                .retryOnConnectionFailure(true)
                .connectTimeout(5, TimeUnit.SECONDS)
                .build();

//        GsonBuilder gson = new GsonBuilder().registerTypeHierarchyAdapter(BaseResponse.class, new ErrorJsonAdapter());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
//                .addConverterFactory(GsonConverterFactory.create(gson.create()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(DirtyJsonConverter.create())
                .build();
        mApi = retrofit.create(BaseApi.class);
    }

    private static Interceptor getNetWorkInterceptor() {
        return chain -> {
            Request request = chain.request();
            Response response = chain.proceed(request);
            String cacheControl = request.cacheControl().toString();
            Response.Builder builder = response.newBuilder();
            if (hasNetwork(App.getContext())) {
                LogUtil.dd("Network is available");
                if (!cacheControl.isEmpty()) {
                    builder.header("Cache-Control", cacheControl);
                }
            } else {
                int maxScale = 60 * 60 * 24;  //1周
                builder.header("Cache-Control", "public, only-if-cached, max-stale=" + maxScale);
            }
            return builder.removeHeader("Pragma").build();
        };
    }

    //Whether the network is available.
    private static boolean hasNetwork(Context context) {
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm != null) {
                NetworkInfo info = cm.getActiveNetworkInfo();
                if (info != null) {
                    return info.isAvailable();
                }
            }
        }
        return false;
    }

    private static Cache getCache() {
        File cacheFile = FileTool.getCacheFile();
        return new Cache(cacheFile, 1024 * 1024 * 100);
    }

    public static RxDoHttpClientTest getInstance() {
        if (sINSTANCE == null) {
            sINSTANCE = new RxDoHttpClientTest();
        }
        return sINSTANCE;
    }

    public Observable<BannerBean> getBanner(){
        return mApi.getBanner();
    }

    public Observable<EventBean> getEvent() {
        return mApi.getEvent();
    }

    private static String getLatestAuthentication() {
        return PrefUtil.getAuthUid() + "|" + PrefUtil.getAuthToken();
//        return PrefUtil.getAuthUid() + "" + PrefUtil.getAuthToken();
    }
}
