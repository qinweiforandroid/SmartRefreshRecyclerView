package com.qw.recyclerview.sample.ui.chat

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.qw.recyclerview.core.BaseViewHolder
import com.qw.recyclerview.core.ItemViewDelegate
import com.qw.recyclerview.core.MultiTypeUseCase
import com.qw.recyclerview.sample.R
import com.qw.recyclerview.smartrefreshlayout.template.SmartListCompat
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * Created by qinwei on 2023/5/27 19:04
 * email: qinwei_it@163.com
 */
class ChatActivity : AppCompatActivity(R.layout.activity_chat) {
    private lateinit var list: SmartListCompat<Message>
    private val senderId = "zhang"
    private val receiverId = "li"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mSmartRefreshLayout = findViewById<SmartRefreshLayout>(R.id.mSmartRefreshLayout)
        val mRecyclerView = findViewById<RecyclerView>(R.id.mRecyclerView)
        val mMultiType = MultiTypeUseCase()
        mMultiType.register(Message.MSG_TXT_IN, MessageInItemViewDelegate())
        mMultiType.register(Message.MSG_TXT_TO, MessageToItemViewDelegate())

        list = object : SmartListCompat<Message>(mRecyclerView, mSmartRefreshLayout) {
            override fun onCreateBaseViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
                return mMultiType.getDelegate(viewType).onCreateViewHolder(parent.context, parent)
            }
        }
        list.modules.add(sendMessage("我是要在交一个月的房租，还是押金就可以了"))
        list.modules.add(receiverMessage("你交一个月的房租吧，你退房时我看一下水电煤结清就退你押金"))
        list.modules.add(sendMessage("我是要在交一个月的房租，还是押金就可以了"))
        list.modules.add(receiverMessage("你交一个月的房租吧，你退房时我看一下水电煤结清就退你押金"))
        list.modules.add(sendMessage("我是要在交一个月的房租，还是押金就可以了"))
        list.modules.add(receiverMessage("你交一个月的房租吧，你退房时我看一下水电煤结清就退你押金"))
        list.adapter.notifyDataSetChanged()
    }

    class MessageInItemViewDelegate : ItemViewDelegate {
        override fun onCreateViewHolder(context: Context, parent: ViewGroup): BaseViewHolder {
            return MessageInHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.activity_chat_text_in_item, parent, false)
            )
        }

        inner class MessageInHolder(itemView: View) : BaseViewHolder(itemView) {
            private lateinit var message: Message
            private val mChatInMsgLabel = itemView.findViewById<TextView>(R.id.mChatInMsgLabel)

            init {
                mChatInMsgLabel.setOnClickListener { }
            }

            override fun initData(position: Int) {
                message = model as Message
                mChatInMsgLabel.text = message.content
            }
        }
    }

    class MessageToItemViewDelegate : ItemViewDelegate {
        override fun onCreateViewHolder(context: Context, parent: ViewGroup): BaseViewHolder {
            return MessageToHolder(
                LayoutInflater.from(context)
                    .inflate(R.layout.activity_chat_text_out_item, parent, false)
            )
        }

        inner class MessageToHolder(itemView: View) : BaseViewHolder(itemView) {
            private lateinit var message: Message
            private val mChatOutMsgLabel = itemView.findViewById<TextView>(R.id.mChatOutMsgLabel)

            init {
                mChatOutMsgLabel.setOnClickListener { }
            }

            override fun initData(position: Int) {
                message = model as Message
                mChatOutMsgLabel.text = message.content
            }
        }
    }

    private fun sendMessage(txt: String): Message {
        return Message(
            senderId = senderId,
            senderName = "张三",
            contentType = MessageType.txt,
            content = txt,
            receiverId = receiverId,
            receiverName = "李四",
            status = StatusType.done,
            timestamp = System.currentTimeMillis()
        )
    }

    private fun receiverMessage(txt: String): Message {
        return Message(
            senderId = receiverId,
            senderName = "李四",
            contentType = MessageType.txt,
            content = txt,
            receiverId = senderId,
            receiverName = "张三",
            status = StatusType.done,
            timestamp = System.currentTimeMillis()
        )
    }
}