package com.permision.annotation

@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PermissionCancel(val requestCode: Int = -1)