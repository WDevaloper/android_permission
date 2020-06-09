package com.permision.permissions

interface IPermissionCallback {
    fun granted()
    fun denied()
    fun shouldShowRequestPermissionRationale(vararg permissions: String)
    fun cancel()
}