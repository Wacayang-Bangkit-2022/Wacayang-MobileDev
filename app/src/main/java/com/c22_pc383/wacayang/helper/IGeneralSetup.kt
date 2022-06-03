package com.c22_pc383.wacayang.helper

interface IGeneralSetup {
    fun setup()
    fun enableControl(isEnabled: Boolean) {}
    fun observerCall() {}
    fun refresh() {}
}