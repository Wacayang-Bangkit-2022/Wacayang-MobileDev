package com.c22_pc383.wacayang.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.c22_pc383.wacayang.R
import com.c22_pc383.wacayang.data.AppPreference
import com.google.firebase.auth.internal.IdTokenListener
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class TokenUpdaterService : Service() {

    override fun onBind(intent: Intent): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Firebase.auth.addIdTokenListener(IdTokenListener { result ->
            val token = result.token
            if (token != null) AppPreference(applicationContext).setToken(token)
            else {
                if (Firebase.auth.currentUser != null)
                    Toast.makeText(applicationContext, getString(R.string.failed_renew_token), Toast.LENGTH_LONG).show()
            }
        })

        return START_STICKY
    }
}