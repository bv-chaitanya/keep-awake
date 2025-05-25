package com.example.keepawake

import android.content.Context
import androidx.core.content.edit

private const val PREFS = "keep_awake_prefs"
private const val KEY_ACTIVE = "is_active"

object KeepAwakeState {
    fun isActive(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY_ACTIVE, false)

    fun setActive(ctx: Context, active: Boolean) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit {
            putBoolean(KEY_ACTIVE, active)
        }
    }
}