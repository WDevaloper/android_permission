package com.permision.uitls


typealias CatchBlock = (ex: Exception) -> Unit
typealias TryBlock = () -> Unit

inline fun tryCatch(tryBlock: TryBlock, noinline catchBlock: CatchBlock? = null) = try {
    tryBlock.invoke()
} catch (ex: Exception) {
    catchBlock?.invoke(ex)
    ex.printStackTrace()
}