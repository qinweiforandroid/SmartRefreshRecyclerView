package com.qw.recyclerview.sample.ui.player

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL

object TvChannelRepository {

    suspend fun loadChannels(): List<TvChannel> = withContext(Dispatchers.IO) {
        val verified = curatedChannels()
        verified.ifEmpty { fallbackChannels() }
    }

    fun fallbackForUi(): List<TvChannel> = fallbackChannels()


    private fun curatedChannels(): List<TvChannel> = listOf(
        TvChannel(
            name = "CCTV-1 综合",
            group = "综合",
            streamUrl = "http://69.30.245.50/live/cctv1.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-2 财经",
            group = "财经",
            streamUrl = "http://74.91.26.218:82/live/cctv2hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-3 综艺",
            group = "综艺",
            streamUrl = "http://74.91.26.218:82/live/cctv3hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-6 电影",
            group = "电影",
            streamUrl = "http://69.30.245.50/live/cctv6.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-7 国防军事",
            group = "军事",
            streamUrl = "http://74.91.26.218:82/live/cctv7hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-8 电视剧",
            group = "电视剧",
            streamUrl = "http://74.91.26.218:82/live/cctv8hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-9 纪录",
            group = "纪录",
            streamUrl = "https://xykt-fix.github.io/Y77.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-10 科教",
            group = "科教",
            streamUrl = "http://74.91.26.218:82/live/cctv10hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-11 戏曲",
            group = "戏曲",
            streamUrl = "http://74.91.26.218:82/live/cctv11hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-12 社会与法",
            group = "法治",
            streamUrl = "http://74.91.26.218:82/live/cctv12hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-14 少儿",
            group = "少儿",
            streamUrl = "http://74.91.26.218:82/live/cctv14hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-15 音乐",
            group = "音乐",
            streamUrl = "https://xykt-fix.github.io/play/a02e/index.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-16 奥林匹克",
            group = "体育",
            streamUrl = "http://74.91.26.218:82/live/cctv16hd.m3u8",
            sourceLabel = "已验活"
        ),
        TvChannel(
            name = "CCTV-17 农业农村",
            group = "农业",
            streamUrl = "http://74.91.26.218:82/live/cctv17hd.m3u8",
            sourceLabel = "已验活"
        )
    )

    private fun fallbackChannels(): List<TvChannel> = listOf(
        TvChannel(
            name = "CCTV-1 综合",
            group = "综合",
            streamUrl = "http://69.30.245.50/live/cctv1.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-2 财经",
            group = "财经",
            streamUrl = "http://74.91.26.218:82/live/cctv2hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-3 综艺",
            group = "综艺",
            streamUrl = "http://74.91.26.218:82/live/cctv3hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-6 电影",
            group = "电影",
            streamUrl = "http://69.30.245.50/live/cctv6.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-7 国防军事",
            group = "军事",
            streamUrl = "http://74.91.26.218:82/live/cctv7hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-8 电视剧",
            group = "电视剧",
            streamUrl = "http://74.91.26.218:82/live/cctv8hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-9 纪录",
            group = "纪录",
            streamUrl = "https://xykt-fix.github.io/Y77.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-10 科教",
            group = "科教",
            streamUrl = "http://74.91.26.218:82/live/cctv10hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-11 戏曲",
            group = "戏曲",
            streamUrl = "http://74.91.26.218:82/live/cctv11hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-12 社会与法",
            group = "法治",
            streamUrl = "http://74.91.26.218:82/live/cctv12hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-14 少儿",
            group = "少儿",
            streamUrl = "http://74.91.26.218:82/live/cctv14hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-15 音乐",
            group = "音乐",
            streamUrl = "https://xykt-fix.github.io/play/a02e/index.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-16 奥林匹克",
            group = "体育",
            streamUrl = "http://74.91.26.218:82/live/cctv16hd.m3u8",
            sourceLabel = "回退源"
        ),
        TvChannel(
            name = "CCTV-17 农业农村",
            group = "农业",
            streamUrl = "http://74.91.26.218:82/live/cctv17hd.m3u8",
            sourceLabel = "回退源"
        )
    )
}
