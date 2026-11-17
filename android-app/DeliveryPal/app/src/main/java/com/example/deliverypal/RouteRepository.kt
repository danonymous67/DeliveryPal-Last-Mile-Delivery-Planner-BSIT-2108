package com.example.deliverypal

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.mapsforge.core.model.LatLong
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

object RouteRepository {

    private const val TAG = "RouteRepository"

    /**
     * Fetches real road route geometry from free OSRM public service.
     * Returns a list of LatLong coordinates along actual streets/roads.
     */
    suspend fun fetchRoadRoute(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double
    ): List<LatLong> = withContext(Dispatchers.IO) {
        if (startLat == 0.0 || startLng == 0.0 || endLat == 0.0 || endLng == 0.0) {
            return@withContext emptyList()
        }

        try {
            val urlString = String.format(
                Locale.US,
                "https://router.project-osrm.org/route/v1/driving/%.6f,%.6f;%.6f,%.6f?overview=full&geometries=geojson",
                startLng, startLat, endLng, endLat
            )

            Log.d(TAG, "Fetching route from OSRM: $urlString")

            val url = URL(urlString)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 8000
                readTimeout = 8000
                setRequestProperty("User-Agent", "DeliveryPalApp/1.0 (Android)")
            }

            val responseCode = connection.responseCode
            Log.d(TAG, "OSRM response code: $responseCode")

            if (responseCode == HttpURLConnection.HTTP_OK) {
                val jsonText = connection.inputStream.bufferedReader().use { it.readText() }
                val jsonObject = JSONObject(jsonText)
                val routes = jsonObject.optJSONArray("routes")
                if (routes != null && routes.length() > 0) {
                    val route = routes.getJSONObject(0)
                    val geometry = route.optJSONObject("geometry")
                    val coordinates = geometry?.optJSONArray("coordinates")
                    if (coordinates != null) {
                        val points = mutableListOf<LatLong>()
                        for (i in 0 until coordinates.length()) {
                            val coord = coordinates.getJSONArray(i)
                            val lng = coord.getDouble(0)
                            val lat = coord.getDouble(1)
                            points.add(LatLong(lat, lng))
                        }
                        Log.d(TAG, "Successfully fetched ${points.size} road route waypoints!")
                        return@withContext points
                    }
                }
            } else {
                Log.e(TAG, "OSRM request failed with response code $responseCode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching road route from OSRM", e)
        }

        // Fallback if offline or network error: return direct endpoints
        return@withContext listOf(LatLong(startLat, startLng), LatLong(endLat, endLng))
    }
}
