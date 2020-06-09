package com.permision.permissions

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AlertDialog
import androidx.collection.SimpleArrayMap
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.fragment.app.Fragment
import com.permision.permissions.menu.IMenu
import com.permision.permissions.menu.impl.DefaultStartSettings
import com.permision.permissions.menu.impl.VivoStartSettings
import java.lang.Exception
import java.util.*

object PermissionUtils {
    private val MIN_SDK_PERMISSIONS: SimpleArrayMap<String, Int> = SimpleArrayMap(8)

    init {
        // 保存最小sdk即minSdk对应的权限才可以使用，如：满足其最低API级别要求
        // 也就是说如果当前设备sdk 小于 该权限的的最低sdk要求，那么这个权限是不可能申请成功
        MIN_SDK_PERMISSIONS.put(Manifest.permission.ADD_VOICEMAIL, 14)//最低要求是14
        MIN_SDK_PERMISSIONS.put(Manifest.permission.BODY_SENSORS, 20)//最低要求是20
        MIN_SDK_PERMISSIONS.put(Manifest.permission.READ_CALL_LOG, 16)//最低要求是16
        MIN_SDK_PERMISSIONS.put(Manifest.permission.READ_EXTERNAL_STORAGE, 16)//最低要求是16
        MIN_SDK_PERMISSIONS.put(Manifest.permission.USE_SIP, 9)//最低要求是9
        MIN_SDK_PERMISSIONS.put(Manifest.permission.WRITE_CALL_LOG, 16)//最低要求是16
        MIN_SDK_PERMISSIONS.put(Manifest.permission.SYSTEM_ALERT_WINDOW, 23)//最低要求是23
        MIN_SDK_PERMISSIONS.put(Manifest.permission.WRITE_SETTINGS, 23)//最低要求是23
    }

    private const val MANUFACTURER_DEFAULT = "default"
    private const val MANUFACTURER_HUAWEI = "huawei"
    private const val MANUFACTURER_OPPO = "oppo"
    private const val MANUFACTURER_VIVO = "vivo"
    private const val MANUFACTURER_xiaomi = "xiaomi"

    private val PERMISSION_MENU: HashMap<String, Class<out IMenu>> = HashMap()

    init {
        PERMISSION_MENU[MANUFACTURER_VIVO] = VivoStartSettings::class.java
        PERMISSION_MENU[MANUFACTURER_DEFAULT] = DefaultStartSettings::class.java
    }


    private val PERMISSION_TIPS: HashMap<String, String> = HashMap()

    init {
        PERMISSION_TIPS[Manifest.permission.WRITE_SETTINGS] = "允许读取或写入系统设置"
        PERMISSION_TIPS[Manifest.permission.WRITE_EXTERNAL_STORAGE] = "允许写入外部存储"
        PERMISSION_TIPS[Manifest.permission.READ_EXTERNAL_STORAGE] = "允许读取外部存储"
        PERMISSION_TIPS[Manifest.permission.WRITE_CONTACTS] = "允许写入联系人,但不可读取"
        PERMISSION_TIPS[Manifest.permission.READ_CONTACTS] = "允许访问联系人通讯录信息"
        PERMISSION_TIPS[Manifest.permission.READ_CALL_LOG] = "允许读取通话记录"
        PERMISSION_TIPS[Manifest.permission.RECORD_AUDIO] = "允许录制声音"
        PERMISSION_TIPS[Manifest.permission.CAMERA] = "允许访问摄像头进行拍照"
        PERMISSION_TIPS[Manifest.permission.READ_PHONE_STATE] = "允许访问电话状态"
    }


    /**
     *
     *
     * 请查看{@link .PermissionActivity.onRequestPermissionsResult}方法
     *
     * @param grantResults results
     *
     * @return 如果已授予所有权限，则返回true。
     */
    @JvmStatic
    fun verifyPermissions(vararg grantResults: Int): Boolean {
        if (grantResults.isEmpty()) return false
        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) return false
        }
        return true
    }


    /**
     * @param context     context
     * @param permissions permission list
     *
     * @return 如果Activity或Fragment有权访问所有给定的权限，则返回true。
     */
    fun hasSelfPermissions(context: Context, vararg permissions: String): Boolean {
        for (permission in permissions) {
            if (permissionExists(permission) &&
                !hasSelfPermission(context, permission)
            ) return false
        }
        return true
    }

    /**
     *
     * 实际上是要过滤点，系统API的隐藏权限，即不可访问的权限
     *
     * @param permission permission
     * @return 如果此SDK版本中存在权限，则返回true
     */
    private fun permissionExists(permission: String): Boolean {
        // 检查此设备上的权限是否可能不可用的
        val minVersion = MIN_SDK_PERMISSIONS.get(permission)
        // 2、如果上述调用返回了null，则无需对设备API级别的权限进行检查
        // 否则，我们检查是否满足其最低API级别要求，也就是说如果当前设备sdk<该权限的的最低要求那么是不可能申请成功
        return minVersion == null || Build.VERSION.SDK_INT >= minVersion
    }

    @SuppressLint("WrongConstant")
    private fun hasSelfPermission(context: Context, permission: String): Boolean {
        return try {
            PermissionChecker.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        } catch (t: RuntimeException) {
            false
        }
    }

    /**
     * Checks given permissions are needed to show rationale.
     *
     * @param activity    activity
     * @param permissions permission list
     * @return returns true if one of the permission is needed to show rationale.
     */
    fun shouldShowRequestPermissionRationale(
        activity: Activity,
        vararg permissions: String
    ): Boolean {
        for (permission in permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                return true
            }
        }
        return false
    }

    /**
     * Checks given permissions are needed to show rationale.
     *
     * @param fragment    fragment
     * @param permissions permission list
     * @return returns true if one of the permission is needed to show rationale.
     */
    fun shouldShowRequestPermissionRationale(
        fragment: Fragment,
        vararg permissions: String
    ): Boolean {
        for (permission in permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {
                return true
            }
        }
        return false
    }


    /**
     * 过滤权限，提供解释给用户
     */
    private fun permissionFilter(
        context: Context,
        vararg permissions: String
    ): String {
        if (permissions.isEmpty()) return ""
        val buffer = StringBuffer("")
        permissions.forEachIndexed { index, permission ->
            if (!hasSelfPermission(context, permission)) {
                buffer.append("1、").append(PERMISSION_TIPS[permission] ?: "").append(";")
                if (index != permissions.size - 1) buffer.append("\n")
            }
        }
        return buffer.toString()
    }


    // 跳转设置
    //todo 过滤那些权限没有申请成功，并提示需要那些权限，让用户去设置页面开启
    fun startAndroidSettings(context: Context, vararg permissions: String) {
        AlertDialog.Builder(context)
            .setTitle("如需正常使用请您允许以下权限:")
            .setMessage(permissionFilter(context, *permissions))
            .setCancelable(false).setNegativeButton("取消", null)
            .setPositiveButton("去设置") { dialog, _ ->
                try {
                    dialog.dismiss()
                    var clazz = PERMISSION_MENU[Build.MANUFACTURER.toLowerCase(Locale.US)]
                    if (clazz == null) clazz = PERMISSION_MENU[MANUFACTURER_DEFAULT]
                    val intent = clazz?.newInstance()?.getIntent(context)
                    if (intent != null) context.startActivity(intent)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }.show()
    }
}