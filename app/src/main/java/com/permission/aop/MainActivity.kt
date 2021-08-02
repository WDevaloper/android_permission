package com.permission.aop

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.permision.PermissionCall
import com.permision.annotation.Check
import com.permision.annotation.Permission
import com.permision.annotation.PermissionDenied
import com.permision.annotation.PermissionDescription

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    @Check(LoginCheckStatus::class, code = 200)
    fun click(view: View) {
        Log.e("tag", "click")
    }


    fun click2(view: View) {
        Log.e("tag", "click2")
        testPermision()
    }


    fun click3(view: View) {
        testPermision1()
    }


    @PermissionDescription(200)
    private fun permissionDescription1(call: PermissionCall) {
        Log.e("tag", "permissionDescription1")
        onPermissionDescription(call)
    }

    @Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 200)
    private fun testPermision() {
        Log.e("tag", "Manifest.permission.WRITE_EXTERNAL_STORAGE")
    }


    @PermissionDenied(200)
    private fun dPermision() {
        Log.e("tag", "dPermision")
    }


    @PermissionDescription(201)
    private fun permissionDescription2(call: PermissionCall) {
        Log.e("tag", "permissionDescription2")
        onPermissionDescription(call)
    }

    @Permission(Manifest.permission.CAMERA, requestCode = 201)
    private fun testPermision1() {
        Log.e("tag", "CAMERA")
    }

    @PermissionDenied(201)
    private fun dPermision2() {
        Log.e("tag", "dPermision2")
    }


    private fun onPermissionDescription(call: PermissionCall) {
        Log.e("tag", "onPermissionDescription")

        AlertDialog.Builder(this)
            .setTitle("隐私协议")
            .setMessage(
                "请您了解，您需要注册成为优必上用户方可使用本软件的扫码购物、网上购物等功能，" +
                        "在您注册前仍然可以浏览本软件中商品和服务内容。"
            )
            .setPositiveButton("确定") { _, _ -> call.invoke() }
            .setNegativeButton("取消", null)
            .create()
            .show()
    }


}
