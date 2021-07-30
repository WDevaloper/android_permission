package com.permision.annotation


/**
 * 被标注的方法必须 得 带参数PermissionDescriptionCallback
 */
@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PermissionDescription
