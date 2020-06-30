package com.permission.aop

import android.content.Context
import android.os.Handler
import com.permision.aop.CheckStatus

class LoginCheckStatus : CheckStatus {
    var isLogin = false
    override fun doCheck(context: Context?, code: Int): Boolean {
        Handler().postDelayed({
            isLogin = !isLogin
        }, 5000)
        return isLogin
    }
}