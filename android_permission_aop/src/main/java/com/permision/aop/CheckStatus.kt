package com.permision.aop

import android.content.Context


/**
 * @Describe: 将逻辑交给子类实现
 *
 * @Demo
 *
@Check(CheckLoginStatus::class)
private fun test() {
Log.e("tag","aspectj test")
}
 *
 * @Author: wfy
 */
interface CheckStatus {
    /**
     * 检查状态
     *
     * @return true表示检查通过，false表示检查不通过
     */
    fun doCheck(context: Context?, code: Int): Boolean
}
