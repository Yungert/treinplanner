package com.yungert.treinplanner.ui.tile

import androidx.wear.tiles.DimensionBuilders
import androidx.wear.tiles.LayoutElementBuilders
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.ResourceBuilders
import androidx.wear.tiles.TileBuilders
import androidx.wear.tiles.TileService
import androidx.wear.tiles.TimelineBuilders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.guava.future

private const val RESOURCES_VERSION = "1"

class ReisTile: TileService() {

    private val serviceScope = CoroutineScope(Dispatchers.IO)
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest) = serviceScope.future {
        val singleTileTimeline = TimelineBuilders.Timeline.Builder()
            .addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(
                        LayoutElementBuilders.Layout.Builder()
                            .setRoot(tileLayout())
                            .build()
                    )
                    .build()
            )
            .build()

        return@future TileBuilders.Tile.Builder()
            .setResourcesVersion(RESOURCES_VERSION)
            .setTimeline(singleTileTimeline)
            .build()
    }

    override fun onResourcesRequest(requestParams: RequestBuilders.ResourcesRequest) = serviceScope.future {
        ResourceBuilders.Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .build()
    }


    private fun tileLayout(): LayoutElementBuilders.LayoutElement {
        val text = "test1234"
        return LayoutElementBuilders.Box.Builder()
            .setVerticalAlignment(LayoutElementBuilders.VERTICAL_ALIGN_CENTER)
            .setWidth(DimensionBuilders.expand())
            .setHeight(DimensionBuilders.expand())
            .addContent(
                LayoutElementBuilders.Text.Builder()
                    .setText(text)
                    .build()
            )
            .build()
    }
}