package com.qw.recyclerview.sample

import android.app.Application
import com.qw.network.env.AbstractHost
import com.qw.network.env.Env
import com.qw.recyclerview.core.SRLog
import com.qw.recyclerview.sample.repository.API
import com.qw.recyclerview.sample.repository.RequestManager
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.header.ClassicsHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import java.util.concurrent.TimeUnit

/**
 * Created by qinwei on 2021/7/5 11:54
 */
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SRLog.setDebug(true)
        initHttp()
    }

    private fun initHttp() {
        AbstractHost.setEnv(Env.ENV_RELEASE)
        val okHttp = OkHttpClient.Builder()
            .authenticator(object : Authenticator {
                override fun authenticate(route: Route?, response: Response): Request? {
                    val newBuilder = response.request.newBuilder()
                    newBuilder.addHeader("token", "new token")
                    return newBuilder.build()
                }
            })
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .apply {
                if (BuildConfig.DEBUG) {
//                    val loggingInterceptor = HttpLoggingInterceptor()
//                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//                    addInterceptor(loggingInterceptor)
                    addInterceptor(requestInterceptor)
//                    addInterceptor(responseInterceptor)
                }
            }
        RequestManager.putOkHttpClient(API.domain, okHttp.build())
    }

    private val requestInterceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
                .newBuilder()
                .apply {
//                    if (Session.isLogin) {
//                        addHeader("Authorization", Session.getAuthorization())
//                    }
                }.build()
            return chain.proceed(request)
        }
    }

    companion object {
        init {
//        //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(R.color.black, R.color.white) //全局设置主题颜色
                ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout -> //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter(context).setDrawableSize(20f)
            }
        }
    }
}