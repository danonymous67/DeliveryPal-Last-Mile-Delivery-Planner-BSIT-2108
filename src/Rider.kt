import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

class Rider(
    name: String,
    phone: String,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) : User(name, phone) {

    fun updateCoordinates(newLat: Double, newLong: Double) {
        if (newLat in -90.0..90.0 && newLong in -180.0..180.0) {
            latitude = newLat
            longitude = newLong
        }
    }

    fun displayCoordinates() {
        println("Latitude: $latitude")
        println("Longitude: $longitude")
    }

    /**
     * Calculates distance to target coordinates in kilometers using Haversine formula.
     */
    fun distanceTo(targetLat: Double, targetLng: Double): Double {
        val earthRadiusKm = 6371.0
        val dLat = Math.toRadians(targetLat - latitude)
        val dLng = Math.toRadians(targetLng - longitude)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(latitude)) * cos(Math.toRadians(targetLat)) *
                sin(dLng / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadiusKm * c
    }
}
