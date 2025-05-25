package com.example.keepawake

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class KeepAwakeAppWidgetProvider : AppWidgetProvider() {
    companion object { private const val ACTION_TOGGLE = "WIDGET_TOGGLE" }

    override fun onUpdate(ctx: Context, mgr: AppWidgetManager, ids: IntArray) {
        ids.forEach { update(ctx, mgr, it) }
    }

    override fun onReceive(ctx: Context, intent: Intent) {
        super.onReceive(ctx, intent)
        if (intent.action == ACTION_TOGGLE) {
            if (KeepAwakeState.isActive(ctx)) KeepAwakeService.stop(ctx)
            else KeepAwakeService.start(ctx)
            val mgr = AppWidgetManager.getInstance(ctx)
            val ids = mgr.getAppWidgetIds(ComponentName(ctx, KeepAwakeAppWidgetProvider::class.java))
            onUpdate(ctx, mgr, ids)
        }
    }

    private fun update(ctx: Context, mgr: AppWidgetManager, widgetId: Int) {
        val views = RemoteViews(ctx.packageName, R.layout.widget_keep_awake)
        val active = KeepAwakeState.isActive(ctx)
        views.setTextViewText(
            R.id.txt_state,
            ctx.getString(if (active) R.string.keep_awake_state_on else R.string.keep_awake_state_off)
        )
        val pi = PendingIntent.getBroadcast(
            ctx, widgetId,
            Intent(ctx, KeepAwakeAppWidgetProvider::class.java).setAction(ACTION_TOGGLE),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.txt_state, pi)
        mgr.updateAppWidget(widgetId, views)
    }
}
