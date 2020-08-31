package com.tarlad.client.repos.impl

import android.os.AsyncTask
import com.google.gson.Gson
import com.tarlad.client.AppSession
import com.tarlad.client.dao.UserDao
import com.tarlad.client.models.db.User
import com.tarlad.client.repos.ImageRepo
import io.reactivex.rxjava3.core.Single
import io.socket.client.Ack
import io.socket.client.Socket
import org.json.JSONObject
import java.util.*

class ImageRepoImpl(
    private val socket: Socket,
    private val appSession: AppSession,
    private val userDao: UserDao
) : ImageRepo {

    override fun saveImage(data: String) {
        val sendData = JSONObject()
        sendData.put("imageData", data)
        val p = "${appSession.userId}_${Date().time}.jpg"
        sendData.put("path", p)
        socket.emit("image", sendData, Ack {
            val user: User = Gson().fromJson(it[0].toString(), User::class.java)
            userDao.insert(user)
        })
    }

    override fun removeImage() {
        AsyncTask.execute {
            userDao.getById(appSession.userId ?: return@execute)?.imageURL ?: return@execute
            socket.emit("image/delete", Ack {
                val user: User = Gson().fromJson(it[0].toString(), User::class.java)
                userDao.insert(user)
            })
        }
    }
}