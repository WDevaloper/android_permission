package com.permision.permissions.menu

import android.content.Context
import android.content.Intent

interface IMenu {
    fun getIntent(context: Context): Intent
}