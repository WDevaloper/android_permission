package com.permision.uitls

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.view.View
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import java.lang.RuntimeException


/**
 * Aspectj 获取Context对象
 *
 */
object PermissionContextWrapper {
    private var appContext: Context? = null

    @JvmStatic
    fun initContext(context: Context) {
        appContext = context.applicationContext
    }

    /**
     * 如果不初始化将返回null
     */
    @JvmStatic
    fun getContext(): Context? = appContext


    /**
     * context没有任何作用，只是为了启动activity,如果你现在不是context环境中使用，就必须调用初始化方法
     */
    @JvmStatic
    @Throws(RuntimeException::class)
    fun findContext(obj: Any): Context {
        return when (obj) {
            is Context -> obj
            is Activity -> obj
            is Fragment -> obj.requireActivity() // 在Fragment中使用
            is android.app.Fragment -> obj.activity // 在Fragment中使用
            is View -> obj.context // 在View中使用
            is Dialog -> obj.context // 在Dialog中使用
            is PopupWindow -> obj.contentView.context
            else -> return appContext
                ?: throw RuntimeException("PermissionContextWrapper IS Around Joint Point Error:context == null")
        }
    }
}