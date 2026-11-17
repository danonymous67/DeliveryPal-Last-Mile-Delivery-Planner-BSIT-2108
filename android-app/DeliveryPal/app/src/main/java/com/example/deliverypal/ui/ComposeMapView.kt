package com.example.deliverypal.ui

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.deliverypal.MapUtils
import com.example.deliverypal.Rider
import org.mapsforge.core.graphics.Canvas
import org.mapsforge.core.graphics.Style
import org.mapsforge.core.model.BoundingBox
import org.mapsforge.core.model.LatLong
import org.mapsforge.core.model.Point
import org.mapsforge.core.model.Rotation
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import org.mapsforge.map.android.util.AndroidUtil
import org.mapsforge.map.android.view.MapView
import org.mapsforge.map.layer.Layer
import org.mapsforge.map.layer.overlay.Marker
import org.mapsforge.map.layer.overlay.Polyline
import org.mapsforge.map.layer.renderer.TileRendererLayer
import org.mapsforge.map.reader.MapFile
import org.mapsforge.map.rendertheme.internal.MapsforgeThemes

class MapTapLayer(
    private val onTapCallback: (LatLong) -> Unit
) : Layer() {
    override fun draw(
        boundingBox: BoundingBox?,
        zoomLevel: Byte,
        canvas: Canvas?,
        point: Point?,
        rotation: Rotation?
    ) {
        // Overlay tap detector - no drawing needed
    }

    override fun onTap(tapLatLong: LatLong, layerXY: Point?, tapXY: Point?): Boolean {
        onTapCallback(tapLatLong)
        return true
    }
}

@Composable
fun ComposeMapView(
    modifier: Modifier = Modifier,
    pickupLat: Double? = null,
    pickupLng: Double? = null,
    destLat: Double? = null,
    destLng: Double? = null,
    routePoints: List<LatLong> = emptyList(),
    assignedRider: Rider? = null,
    allRiders: List<Rider> = emptyList(),
    onTapLocation: ((Double, Double) -> Unit)? = null
) {
    val context = LocalContext.current

    // Cached pin bitmaps
    val pickupPin = remember(context) { MapUtils.createPinBitmap(context, Color.parseColor("#2E7D32"), "A") }
    val destPin = remember(context) { MapUtils.createPinBitmap(context, Color.parseColor("#C62828"), "B") }
    val assignedRiderPin = remember(context) { MapUtils.createPinBitmap(context, Color.parseColor("#1565C0"), "R") }
    val idleRiderPin = remember(context) { MapUtils.createPinBitmap(context, Color.parseColor("#78909C"), "R") }

    AndroidView(
        factory = { ctx ->
            MapView(ctx).apply {
                mapScaleBar.isVisible = true
                setBuiltInZoomControls(true)

                try {
                    val mapFile = MapUtils.getMapFile(ctx)
                    val tileCache = AndroidUtil.createTileCache(
                        ctx,
                        "mapcache",
                        model.displayModel.tileSize,
                        1f,
                        model.frameBufferModel.overdrawFactor
                    )

                    val mapDataStore = MapFile(mapFile)
                    val tileRendererLayer = TileRendererLayer(
                        tileCache,
                        mapDataStore,
                        model.mapViewPosition,
                        AndroidGraphicFactory.INSTANCE
                    )
                    tileRendererLayer.cacheTileMargin = 1
                    tileRendererLayer.cacheZoomMinus = 1
                    tileRendererLayer.cacheZoomPlus = 2
                    tileRendererLayer.setXmlRenderTheme(MapsforgeThemes.DEFAULT)

                    layerManager.layers.add(tileRendererLayer)

                    // Default center & zoom limit
                    setCenter(LatLong(MapUtils.DEFAULT_LAT, MapUtils.DEFAULT_LNG))
                    setZoomLevel(MapUtils.DEFAULT_ZOOM)

                    // Lock map position limit within hometown bounds (14.050 - 14.100 Lat, 120.610 - 120.660 Lng)
                    val hometownBounds = BoundingBox(14.050, 120.610, 14.100, 120.660)
                    model.mapViewPosition.setMapLimit(hometownBounds)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        },
        update = { mapView ->
            try {
                val layers = mapView.layerManager.layers
                while (layers.size() > 1) {
                    layers.remove(1)
                }

                // Add tap layer if interactive tap is enabled
                onTapLocation?.let { callback ->
                    layers.add(MapTapLayer { latLong ->
                        callback(latLong.latitude, latLong.longitude)
                    })
                }

                // Highlight road route line in BRIGHT LIGHT BLUE (#00B0FF)
                if (routePoints.isNotEmpty()) {
                    val linePaint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
                        color = AndroidGraphicFactory.INSTANCE.createColor(255, 0, 176, 255) // Bright Light Blue
                        strokeWidth = 12f
                        setStyle(Style.STROKE)
                    }
                    val polyline = Polyline(linePaint, AndroidGraphicFactory.INSTANCE)
                    polyline.latLongs.addAll(routePoints)
                    layers.add(polyline)
                } else if (pickupLat != null && pickupLng != null && destLat != null && destLng != null &&
                    pickupLat != 0.0 && pickupLng != 0.0 && destLat != 0.0 && destLng != 0.0) {
                    // Fallback direct line in Bright Light Blue
                    val linePaint = AndroidGraphicFactory.INSTANCE.createPaint().apply {
                        color = AndroidGraphicFactory.INSTANCE.createColor(255, 0, 176, 255) // Bright Light Blue
                        strokeWidth = 12f
                        setStyle(Style.STROKE)
                    }
                    val polyline = Polyline(linePaint, AndroidGraphicFactory.INSTANCE)
                    polyline.latLongs.add(LatLong(pickupLat, pickupLng))
                    polyline.latLongs.add(LatLong(destLat, destLng))
                    layers.add(polyline)
                }

                // Pickup (Point A) marker
                if (pickupLat != null && pickupLng != null && pickupLat != 0.0 && pickupLng != 0.0) {
                    val pos = LatLong(pickupLat, pickupLng)
                    layers.add(Marker(pos, pickupPin, 0, -pickupPin.height / 2))
                }

                // Destination (Point B) marker
                if (destLat != null && destLng != null && destLat != 0.0 && destLng != 0.0) {
                    val pos = LatLong(destLat, destLng)
                    layers.add(Marker(pos, destPin, 0, -destPin.height / 2))
                }

                // All riders or assigned rider markers
                allRiders.forEach { r ->
                    if (r.latitude != 0.0 && r.longitude != 0.0) {
                        val isAssigned = (assignedRider?.name == r.name)
                        val pin = if (isAssigned) assignedRiderPin else idleRiderPin
                        val pos = LatLong(r.latitude, r.longitude)
                        layers.add(Marker(pos, pin, 0, -pin.height / 2))
                    }
                }

                mapView.repaint()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        },
        modifier = modifier
    )
}
