package com.qw.recyclerview.core

import android.util.Log

/**
 * Created by qinwei on 2021/7/6 13:37
 */
class SRLog {
    companion object {
        var debug = false
        fun d(msg: String) {
            if (debug) {
                Log.d("SmartRefresh", msg)
            }
        }
    }
}