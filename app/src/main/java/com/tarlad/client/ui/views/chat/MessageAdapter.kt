package com.tarlad.client.ui.views.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tarlad.client.R
import com.tarlad.client.models.Message
import com.tarlad.client.models.User
import kotlinx.android.synthetic.main.message_frame.view.*
import kotlinx.android.synthetic.main.message_from_me.view.*
import kotlinx.android.synthetic.main.message_to_me.view.*
import kotlinx.android.synthetic.main.message_to_me_textedit.view.*

class MessagesAdapter(val messages: ArrayList<Message> = arrayListOf(), val users: ArrayList<User> = arrayListOf(), var userId: Long = -1) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>(){

    var array: ArrayList<ArrayList<Message>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.message_frame, parent, false)

        return ViewHolder(view, users, userId)
    }

    override fun getItemCount(): Int {
        var tmp: ArrayList<Message> = ArrayList()
        var time: Long = 0
        var author = ""
        array.clear()
        messages.forEach { message ->
            if (author != message.userId.toString() || time - message.time > 60_000){
                tmp = ArrayList<Message>().apply { add(message) }
                array.add(tmp)
            }else{
                tmp.add(message)
            }
            time = message.time
            author = message.userId.toString()
        }
        return array.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val message = array[position]

        holder.showNickname = position == array.size - 1 || array[position + 1].first().userId != message.first().userId
        holder.messages = message
    }

    class ViewHolder(val view: View, val users: List<User>, val userId: Long): RecyclerView.ViewHolder(view){

        var showNickname: Boolean = true

        var messages: ArrayList<Message> = ArrayList()
            set(value) {

                field = value

                view.message_frame.removeAllViews()

                if (messages.isEmpty()) return

                if (messages.first().userId != userId){

                    val messagesToMeList = LayoutInflater.from(view.context).inflate(R.layout.message_to_me, view.message_frame)

                    if (showNickname){
                        messagesToMeList.nickname.text = users.find { user -> user.id == messages.first().userId }?.nickname
                    }else{
                        messagesToMeList.nickname.visibility = View.GONE
                    }

                    value.reversed().forEach {
                        val txt = LayoutInflater.from(view.context).inflate(R.layout.message_to_me_textedit, messagesToMeList.messages_to_me, false)
                        txt.message.text = it.data
                        messagesToMeList.messages_to_me.addView(txt)
                    }

                    Glide.with(messagesToMeList)
                        .load("https://picsum.photos/200")
                        .into(messagesToMeList.imageView)

                }else{

                    val messagesFromMeList = LayoutInflater.from(view.context).inflate(R.layout.message_from_me, view.message_frame)

                    value.reversed().forEach {
                        val txt = LayoutInflater.from(view.context).inflate(R.layout.message_from_me_textedit, messagesFromMeList.messages_from_me, false)
                        txt.message.text = it.data
                        messagesFromMeList.messages_from_me.addView(txt)
                    }

                }
            }
    }
}