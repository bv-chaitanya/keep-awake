package com.example.keepawake

import android.service.quicksettings.Tile
import android.service.quicksettings.TileService

class KeepAwakeTileService : TileService() {
    override fun onStartListening() {
        qsTile.state = if (KeepAwakeState.isActive(this)) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        qsTile.updateTile()
    }
    override fun onClick() {
        super.onClick()
        if (KeepAwakeState.isActive(this)) {
            KeepAwakeService.stop(this)
            qsTile.state = Tile.STATE_INACTIVE
        } else {
            KeepAwakeService.start(this)
            qsTile.state = Tile.STATE_ACTIVE
        }
        qsTile.updateTile()
    }
}
