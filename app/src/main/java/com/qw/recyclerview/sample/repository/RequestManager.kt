package com.qw.recyclerview.sample.repository

import androidx.collection.ArrayMap
import okhttp3.*
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.IllegalArgumentException
import java.lang.ref.SoftReference

/**
 * Created by qinwei on 2019-11-29 18:00
 * email: qinwei_it@163.com
 */
object RequestManager {

    private val mRetrofitCache = ArrayMap<String, SoftReference<Retrofit>>()

    private val mOkHttpClientCache = ArrayMap<String, OkHttpClient>()

    fun putOkHttpClient(domain: String, okHttpClient: OkHttpClient) {
        if (!mOkHttpClientCache.contains(domain)) {
            mOkHttpClientCache[domain] = okHttpClient
        }
    }

    fun build(
        domain: String,
        ok: OkHttpClient,
        factory: CallAdapter.Factory? = null
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(domain)
            .addConverterFactory(GsonConverterFactory.create())
            .apply {
                if (factory != null) {
                    addCallAdapterFactory(factory)
                }
            }.client(ok).build()
    }

    fun build(
        domain: String,
        factory: CallAdapter.Factory? = null
    ): Retrofit {
        //check ok config
        if (mOkHttpClientCache[domain] == null) {
            throw IllegalArgumentException("domain:$domain not config OkHttpClient")
        }
        val key = genKey(domain, factory, mOkHttpClientCache[domain])
        val retrofit: Retrofit? = mRetrofitCache[key]?.get()
        if (retrofit != null) {
            return retrofit
        }
        return Retrofit.Builder()
            .baseUrl(domain)
            .addConverterFactory(GsonConverterFactory.create())
            .apply {
                if (factory != null) {
                    addCallAdapterFactory(factory)
                }
            }.client(mOkHttpClientCache[domain]!!).build().apply {
                mRetrofitCache[key] = SoftReference<Retrofit>(this)
            }
    }

    private fun genKey(
        domain: String,
        factory: CallAdapter.Factory?,
        okHttpClient: OkHttpClient?
    ): String {
        return domain + factory?.javaClass?.canonicalName + okHttpClient?.javaClass?.canonicalName
    }
}