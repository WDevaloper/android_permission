package com.permision.annotation

import com.permision.aop.CheckStatus
import kotlin.reflect.KClass

/**
 * @Describe: 检查状态，如登录和网络，一般只有这些条件满足擦会进行下一步
 * @Author: wfy
 */
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
annotation class Check(vararg val value: KClass<out CheckStatus>, val code: Int = -1)//code: 标志你在完成该操作之后需要的操作
