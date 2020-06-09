package com.permision.annotation

@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class PermissionDenied(val requestCode: Int = -1)