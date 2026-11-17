package com.example.deliverypal

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.location.Geocoder
import android.os.Build
import org.mapsforge.core.graphics.Bitmap
import org.mapsforge.map.android.graphics.AndroidGraphicFactory
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

object MapUtils {

    const val MAP_FILE_NAME = "planet_120.625_14.058_d0bcebf2.map"

    // Default Plaza de Roxas, Nasugbu / Batangas location
    const val DEFAULT_LAT = 14.0722
    const val DEFAULT_LNG = 120.6306
    const val DEFAULT_ZOOM: Byte = 15

    /**
     * Copies map file from assets to app cache directory if not already copied.
     */
    fun getMapFile(context: Context): File {
        val targetFile = File(context.cacheDir, MAP_FILE_NAME)
        if (!targetFile.exists()) {
            context.assets.open(MAP_FILE_NAME).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
        }
        return targetFile
    }

    /**
     * Reverses geocoding coordinates to a readable address string.
     */
    fun reverseGeocode(context: Context, lat: Double, lng: Double, onResult: (String, String) -> Unit) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(lat, lng, 1) { addresses ->
                    val address = addresses.firstOrNull()
                    val street = address?.thoroughfare ?: address?.featureName ?: "Selected Pin"
                    val barangay = address?.subLocality ?: address?.locality ?: "Plaza de Roxas Area"
                    onResult(street, barangay)
                }
            } else {
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(lat, lng, 1)
                val address = addresses?.firstOrNull()
                val street = address?.thoroughfare ?: address?.featureName ?: "Selected Pin"
                val barangay = address?.subLocality ?: address?.locality ?: "Plaza de Roxas Area"
                onResult(street, barangay)
            }
        } catch (e: Exception) {
            onResult("Selected Pin", "Plaza de Roxas Area")
        }
    }

    /**
     * Creates a custom Mapsforge Bitmap pin marker with a specific color and label.
     */
    fun createPinBitmap(context: Context, pinColor: Int, label: String): Bitmap {
        val width = 72
        val height = 96
        val androidBitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
        val canvas = Canvas(androidBitmap)

        // Draw pin teardrop body
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = pinColor
            style = Paint.Style.FILL
        }

        val path = Path().apply {
            moveTo(width / 2f, height.toFloat())
            cubicTo(0f, height * 0.6f, 0f, 0f, width / 2f, 0f)
            cubicTo(width.toFloat(), 0f, width.toFloat(), height * 0.6f, width / 2f, height.toFloat())
            close()
        }
        canvas.drawPath(path, paint)

        // Inner white circle
        val innerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawCircle(width / 2f, height * 0.35f, width * 0.28f, innerCirclePaint)

        // Label text
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = 28f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(label, width / 2f, height * 0.43f, textPaint)

        val drawable = android.graphics.drawable.BitmapDrawable(context.resources, androidBitmap)
        return AndroidGraphicFactory.convertToBitmap(drawable)
    }
}
