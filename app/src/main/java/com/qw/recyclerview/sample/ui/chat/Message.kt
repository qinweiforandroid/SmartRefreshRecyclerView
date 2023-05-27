package com.qw.recyclerview.sample.ui.chat

import com.qw.recyclerview.core.IItemViewType

/**
 * Created by qinwei on 2023/5/27 19:56
 * email: qinwei_it@163.com
 */
data class Message(
    val _id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val senderPicture: String = "",
    val receiverId: String = "",
    val receiverName: String = "",
    val receiverPicture: String = "",
    val contentType: MessageType = MessageType.txt,
    val content: String = "",
    val status: StatusType = StatusType.done,
    val timestamp: Long = 0,
    val isRead: Boolean = false,
    val percent: Int = 0
) : IItemViewType {
    companion object {
        const val MSG_TXT_IN = 1
        const val MSG_TXT_TO = 2
        const val MSG_EMO_IN = 3
        const val MSG_EMO_TO = 4
        const val MSG_NOT_SUPPORT = 100
    }

    override fun getItemViewType(): Int {
        return when (senderId) {
            "zhang" -> {
                when (contentType) {
                    MessageType.txt -> {
                        MSG_TXT_TO
                    }

                    MessageType.emo -> {
                        MSG_EMO_TO
                    }

                    else -> {
                        MSG_NOT_SUPPORT
                    }
                }
            }

            else -> {
                when (contentType) {
                    MessageType.txt -> {
                        MSG_TXT_IN
                    }

                    MessageType.emo -> {
                        MSG_EMO_IN
                    }

                    else -> {
                        MSG_NOT_SUPPORT
                    }
                }
            }
        }
    }
}

enum class StatusType {
    ing, done, fail
}

enum class MessageType {
    txt, emo
}