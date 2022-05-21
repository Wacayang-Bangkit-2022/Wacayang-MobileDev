package com.c22_pc383.wacayang.data

import android.content.Context
import android.content.SharedPreferences

internal class AppPreference(ctx: Context) {
    private var pref: SharedPreferences = ctx.getSharedPreferences(APP_PREF_NAME, Context.MODE_PRIVATE)

    fun setCameraFlashPref(isFlashOn: Boolean) = pref.edit().putBoolean(CAM_FLASH_PREF, isFlashOn).apply()
    fun getCameraFlashPref(): Boolean = pref.getBoolean(CAM_FLASH_PREF, false)

    fun setToken(token: String) = pref.edit().putString(USER_TOKEN, token).apply()
    fun getToken(): String = pref.getString(USER_TOKEN, "") ?: ""
    fun clearToken() = pref.edit().remove(USER_TOKEN).apply()

    companion object {
        private const val APP_PREF_NAME = "wacayang_preference"
        private const val CAM_FLASH_PREF = "camera_flash"
        private const val USER_TOKEN = "user_token"
    }
}