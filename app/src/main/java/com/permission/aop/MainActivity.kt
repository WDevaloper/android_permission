package com.permission.aop

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.permision.annotation.Check
import com.permision.annotation.Permission

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    @Check(LoginCheckStatus::class, code = 200)
    fun click(view: View) {
        Log.e("tag","click")
        testPermision()
    }

    @Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 200)
    private fun testPermision() {
        Log.e("tag", "Manifest.permission.WRITE_EXTERNAL_STORAGE")
    }
}
