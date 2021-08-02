package com.permision.annotation


@Target(AnnotationTarget.FUNCTION)
@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
annotation class Permission(vararg val value: String, val requestCode: Int)
