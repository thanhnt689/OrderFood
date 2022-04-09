package com.ntt.orderfood.service

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.ntt.orderfood.HomeActivity
import com.ntt.orderfood.HomeManagerActivity
import com.ntt.orderfood.R
import com.ntt.orderfood.model.Order

class ManagerListenOrderService : Service(), ChildEventListener {

    private lateinit var dataBase: FirebaseDatabase
    private lateinit var myRef: DatabaseReference

    override fun onCreate() {
        super.onCreate()
        dataBase = Firebase.database
        myRef = dataBase.getReference("Order")
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        myRef.addChildEventListener(this)
        return START_NOT_STICKY
    }

    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
        val order = snapshot.getValue<Order>()
        if (order?.status.equals("Placed")) {
            showNotification(snapshot.key, order)
        }
    }

    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

    }

    override fun onChildRemoved(snapshot: DataSnapshot) {

    }

    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

    }

    override fun onCancelled(error: DatabaseError) {

    }

    private fun showNotification(key: String?, order: Order?) {
        val intent = Intent(baseContext, HomeManagerActivity::class.java)
        intent.putExtra("userPhone", order?.phone)
        val contentIntent: PendingIntent =
            PendingIntent.getActivity(baseContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val builder: Notification.Builder = Notification.Builder(baseContext)
        builder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setContentInfo("New Order")
            .setContentText("You have new Order #$key from ${order?.phone}")
            .setContentIntent(contentIntent)
            .setSmallIcon(R.drawable.order_food)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("123", "123", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
            builder.setChannelId(channel.id)
        }
        notificationManager.notify(1, builder.build())
    }
}