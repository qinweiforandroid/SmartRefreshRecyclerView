package com.qw.recyclerview.sample.repository

/**
 * Created by qinwei on 2024/3/20 10:19
 * email: qinwei_it@163.com
 */
data class WanAndroidResponse<T>(val errorCode: Int, val errorMsg: String, val data: T)

data class WanAndroidList<T>(
    val datas: ArrayList<T>?,
    val curPage: Int?,
    val offset: Int?,
    val over: Boolean?,
    val pageCount: Int?,
    val size: Int?,
    val total: Int?
)