package com.permision.aop

import android.content.Context
import android.util.Log
import com.permision.annotation.Check
import com.permision.uitls.PermissionContextWrapper
import com.permision.uitls.tryCatch
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.aspectj.lang.reflect.MethodSignature
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.jvm.isAccessible

/**
 * @Describe: 状态检查AOP,如网络、登录等等
 *
 * @Author: wfy
 */
@Aspect
@SuppressWarnings("unused")
class CheckStatusAspect {
    // 使用WeakHashMap缓存起来，防止每次调用方法都要反射
    private val mCacheStatusClass by lazy {
        WeakHashMap<KClass<out CheckStatus>, CheckStatus>()
    }

    //定义切面的规则
    //1.就在原来应用中哪些注释的地方放到当前切面进行处理,筛选切点
    //execution(注解名   注解用的地方)  ,其他类型的参数使用ars
    //方法名自己定义
    //1、call 和 execution 区别，他们的却别在于织入代码位置不一样，call在PointCut调用处织入代码，而execution则在PointCut内部织入代码
    //2、this 和 target 指向同一个类 。在 call 中， this 和 target 不是指向同一个类 。
    //3、execution 与 call 还有一点很重要的区别。
    // 对于继承类来说，如果它没有覆盖父类的方法， 那么 execution 不会匹配子类中没有覆盖父类的方法。
    // 比如说我们有一个类 B 继承于 A ， 但没有覆盖 A 类的 foo() ，那么对于 B 的实例的 foo() 方法， execution(* B.foo()) 将不会被匹配。
    // 做个总结，如果想跟踪连接点的内部代码运行情况可以考虑使用 execution ，
    // 但如果你只关心连接点的签名（比如你使用第三方库或是标准 API ），则使用 call 。
    @Pointcut("execution(@com.permision.annotation.Check * *(..))")
    fun checkStatus() {
    }

    //2.对进入切面的内容如何处理
    //advice
    //@Before()  在切入点之前运行
    //@After()   在切入点之后运行
    //@Around()  在切入点前后都运行
    //方法名自己定义
    @Around("checkStatus()")
    @Throws(Throwable::class)
    @SuppressWarnings("unused")
    fun aroundJointPoint(joinPoint: ProceedingJoinPoint) {
        val obj = joinPoint.getThis()//被Aspect的对象
        //初始化context
        val context = PermissionContextWrapper.findContext(obj)
        //最后判断这个方法中的注解，是否都满足条件
        if (createInstance(joinPoint, context)) joinPoint.proceed()
    }

    private fun createInstance(
        joinPoint: ProceedingJoinPoint,
        context: Context?
    ): Boolean {
        //是否满足条件
        var isCheckSuccess = false

        //获取方法信息
        val methodSignature = joinPoint.signature as MethodSignature
        methodSignature.method?.isAccessible = true
        val checkAnnotation = methodSignature.method?.getAnnotation(Check::class.java)
        val statusKClass = checkAnnotation?.value

        /*可能需要检查多个条件，如：登录和网络，只有全部成立才会通过*/
        statusKClass?.forEach { it ->
            var statusCheck = mCacheStatusClass[it]
            if (statusCheck == null) {
                tryCatch({
                    //反射创建实现类实例
                    val constructMethod = it.constructors.asSequence().firstOrNull()
                    constructMethod?.isAccessible = true
                    statusCheck = constructMethod?.call()
                    // 优化点，使用缓存避免同一个实例重复反射，造成性能损耗
                    if (statusCheck != null) mCacheStatusClass[it] = statusCheck
                }, {
                    it.printStackTrace()
                    Log.e("CheckStatusAspect", "${it.message}")
                })
            }

            isCheckSuccess = statusCheck?.doCheck(context, checkAnnotation.code) ?: false

            //优化点，如果有存在条件false立即停止
            if (!isCheckSuccess) return isCheckSuccess
        }

        return isCheckSuccess
    }
}

