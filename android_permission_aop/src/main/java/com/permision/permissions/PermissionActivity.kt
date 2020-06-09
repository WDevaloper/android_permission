package com.permision.permissions

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.core.app.ActivityCompat
import java.lang.ref.WeakReference


/**
 *
 *
 * @Permission(Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode = 200)
 * fun testPermission() {
 *   KLogUtil.e("testPermission")
 * }
 *
 * @PermissionDenied(requestCode = 200)
 * fun testPermissionDenied() {
 *   KLogUtil.e("testPermissionDenied")
 * }
 *
 *
 * @PermissionCancel(requestCode = 200)
 * fun testPermissionCancel() {
 *   KLogUtil.e("testPermissionCancel")
 * }
 *
 *
 * 返回 true表示用户拦截ShouldShowRequestRationale
 * @ShouldShowRequestRationale(requestCode = 200)
 * fun testPermissionDeniedAndNotNote()：Boolean {
 *   KLogUtil.e("testPermissionDeniedAndNotNote")
 * }
 *
 */
class PermissionActivity : Activity() {

    @SuppressLint("RtlHardcoded")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //一像素
        window.setGravity(Gravity.LEFT or Gravity.TOP)
        val params = window.attributes
        params.x = 0
        params.y = 0
        params.height = 1
        params.width = 1
        window.attributes = params

        permissions = intent.getStringArrayExtra(PARAM_PERMISSION) ?: arrayOf()
        requestCode = intent.getIntExtra(PARAM_REQUEST_CODE, PARAM_REQUEST_CODE_DEFAULT)
        Log.d(TAG, "permissions=${permissions.joinToString { it }}, requestCode= $requestCode")

        // 申请权限requestCode不能<0会抛异常
        // permissions也不能空
        // mIPermissionCallback回调
        if (permissions.isEmpty() || requestCode < 0 || mIPermissionCallback == null) {
            finish()
            return
        }

        //检查是否已经获取了权限，即用户已经允许的选线
        if (PermissionUtils.hasSelfPermissions(this, *permissions)) {
            mIPermissionCallback?.get()?.granted()
            this.finish()
            return
        }
        // 申请权限
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        //  权限申请成功
        if (PermissionUtils.verifyPermissions(*grantResults)) {
            mIPermissionCallback?.get()?.granted()
            finish()
            return
        }

        // 用户拒绝授权，并设置了不再提醒
        if (!PermissionUtils.shouldShowRequestPermissionRationale(this, *permissions)) {
            mIPermissionCallback?.get()?.shouldShowRequestPermissionRationale(*permissions)
            finish()
            return
        }

        // 用户拒绝授权
        if (!PermissionUtils.verifyPermissions(*grantResults)) {
            mIPermissionCallback?.get()?.denied()
            finish()
            return
        }


        // 用户取消授权
        mIPermissionCallback?.get()?.cancel()
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    private lateinit var permissions: Array<String>
    private var requestCode: Int = PARAM_REQUEST_CODE_DEFAULT


    companion object {
        private val TAG = PermissionActivity::class.java.simpleName
        private const val PARAM_PERMISSION = "param_permission"
        private const val PARAM_REQUEST_CODE = "param_request_code"
        private const val PARAM_REQUEST_CODE_DEFAULT = -1
        @JvmStatic
        private var mIPermissionCallback: WeakReference<IPermissionCallback>? = null

        @JvmStatic
        fun requestPermissionAction(
            context: Context, permissions: Array<out String>,
            requestCode: Int, callback: IPermissionCallback
        ) = Intent(context, PermissionActivity::class.java).let {
            mIPermissionCallback?.clear()
            mIPermissionCallback = WeakReference(callback)
            it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            it.putExtra(PARAM_PERMISSION, permissions)
            it.putExtra(PARAM_REQUEST_CODE, requestCode)
        }.run { ActivityCompat.startActivity(context, this, null) }
    }

}