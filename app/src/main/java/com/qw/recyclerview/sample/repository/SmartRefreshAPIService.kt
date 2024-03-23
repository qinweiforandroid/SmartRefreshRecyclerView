package com.qw.recyclerview.sample.repository

import com.qw.recyclerview.sample.repository.entities.ArticleBean
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by qinwei on 2024/3/20 10:15
 * email: qinwei_it@163.com
 */
interface SmartRefreshAPIService {
    @GET("article/list/{page}/json")
    suspend fun loadArticles(
        @Path("page") page: Int,
        @Query("page_size") pageSize: Int = 20
    ): WanAndroidResponse<WanAndroidList<ArticleBean>>
}