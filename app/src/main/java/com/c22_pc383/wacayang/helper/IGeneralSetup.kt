package com.c22_pc383.wacayang.helper

interface IGeneralSetup {
    fun setup()
    fun isFieldVerified(): Boolean = false
    fun enableControl(isEnabled: Boolean) {}
    fun observerCall() {}
    fun refresh() {}
    fun startAnimation() {}

    companion object {
        const val ANIM_DURATION = 400L
    }
}