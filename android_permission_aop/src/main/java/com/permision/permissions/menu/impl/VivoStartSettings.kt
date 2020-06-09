package com.permision.permissions.menu.impl

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.permision.permissions.menu.IMenu

class VivoStartSettings : IMenu {
    override fun getIntent(context: Context): Intent {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        intent.data = Uri.fromParts("package", context.packageName, null)
        return intent
    }
}