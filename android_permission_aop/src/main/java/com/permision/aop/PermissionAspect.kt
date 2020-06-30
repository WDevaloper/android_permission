package com.permision.aop

import com.permision.annotation.Permission
import com.permision.annotation.PermissionCancel
import com.permision.annotation.PermissionDenied
import com.permision.annotation.ShouldShowRequestRationale
import com.permision.uitls.PermissionContextWrapper
import com.permision.permissions.IPermissionCallback
import com.permision.permissions.PermissionActivity
import com.permision.permissions.PermissionUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import java.lang.reflect.Method


/**
 * AOP是一种面向切面编程，通过预编译方式和运行期动态代理实现程序功能的统一维护的一种技术
 *
 * 比如：Aspectj 也是通过编译器织入动态代理的代码，也就是Aspectj会做一些包装，可能会有轻微的性能损耗，但是可以忽略不计
 */
@Aspect
class
PermissionAspect {
    @Pointcut(
        "execution(@com.permision.annotation.Permission * *(..)) && @annotation(permission)"
    )
    fun permissionMethod(permission: Permission) {//名字和@annotation(permission)保持一致
    }


    @Around("permissionMethod(permission)")//名字和@annotation(permission)保持一致
    @Throws(Throwable::class)
    @SuppressWarnings("unused")
    fun permissionAspect(
        joinPoint: ProceedingJoinPoint,
        permission: Permission
    ) {//名字和@annotation(permission)保持一致

        val obj = joinPoint.getThis()//被Aspect的对象
        val context = PermissionContextWrapper.findContext(obj)  //你可以拿到上下文对象
        PermissionActivity.requestPermissionAction(
            context,
            permission.value,
            permission.requestCode,
            object : IPermissionCallback {
                override fun granted() {
                    joinPoint.proceed(joinPoint.args)
                }

                override fun denied() {
                    handleAction(obj, PermissionDenied::class.java)
                }

                override fun shouldShowRequestPermissionRationale(vararg permissions: String) {
                    handleAction(obj, ShouldShowRequestRationale::class.java, *permissions)
                }

                override fun cancel() {
                    handleAction(obj, PermissionCancel::class.java)
                }
            }
        )
    }

    @Throws(RuntimeException::class)
    private fun handleAction(
        obj: Any,
        annotationClass: Class<out Annotation>,
        vararg permissions: String
    ) {
        val invokeMethod = findInvokeMethod(obj, annotationClass)
        if (invokeMethod != null) {
            //用户定义了接收shouldShowRequestPermissionRationale的方法，
            // 那么如果方法有返回值，并且是Boolean，那么就是表示是否拦截处理，
            // 一般是shouldShowRequestPermissionRationale方法，返回true表示拦截
            var isIntercepted = invokeMethod.invoke(obj)
            // 如果用户不处理，提示用户那么我们需要跳转系统设置
            val isShowRationale = annotationClass == ShouldShowRequestRationale::class.java
            isIntercepted = (isIntercepted is Boolean) && !isIntercepted
            if (isShowRationale && isIntercepted) {
                PermissionUtils.startAndroidSettings(
                    PermissionContextWrapper.findContext(obj),
                    *permissions
                )
            }
        } else if (annotationClass == ShouldShowRequestRationale::class.java) {
            // 用户不定义接收ShouldShowRequestRationale的方法，那么直接默认跳转系统设置
            PermissionUtils.startAndroidSettings(PermissionContextWrapper.findContext(obj), *permissions)
        }
    }

    private fun findInvokeMethod(obj: Any, annotationClass: Class<out Annotation>): Method? {
        var invokeMethod: Method? = null
        obj.javaClass.declaredMethods.asSequence().forEach {
            if (it.isAnnotationPresent(annotationClass)) {
                it.isAccessible = true
                invokeMethod = it
                return invokeMethod  //需要注意的是 return@forEach只是结束本次循环和continue类似，return 就是结束最外层函数，
            }
        }
        return invokeMethod
    }
}