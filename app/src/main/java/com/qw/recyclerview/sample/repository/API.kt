package com.qw.recyclerview.sample.repository

import com.qw.network.env.AbstractHost

/**
 * Created by qinwei on 12/1/20 1:35 PM
 * email: qinwei_it@163.com
 */
object API {
    private val host: AbstractHost = object : AbstractHost() {
        override fun dev(): String {
            return "https://www.wanandroid.com"
        }

        override fun qa(): String {
            return dev()
        }

        override fun beta(): String {
            return qa()
        }

        override fun release(): String {
            return dev()
        }
    }

    @JvmStatic
    val domain: String
        get() = host.host()

    inline fun <reified T> create(): T {
        return RequestManager.build(domain, null).create(T::class.java)
    }
}
