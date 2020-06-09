package com.permission

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.permision.annotation.Permission

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun click(view: View) {
        testPermision()
    }

    @Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 200)
    private fun testPermision() {
        Log.e("tag", "Manifest.permission.WRITE_EXTERNAL_STORAGE")
    }
}
