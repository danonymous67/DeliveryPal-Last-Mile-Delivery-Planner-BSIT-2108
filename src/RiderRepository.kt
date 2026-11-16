object RiderRepository {
    // 5 pre-coded riders positioned directly on paved roads around Plaza de Roxas
    val riders: List<Rider> = listOf(
        Rider("Johann Perez", "09171234567", 14.0735, 120.6300),
        Rider("Michael Hermano", "09182345678", 14.0710, 120.6320),
        Rider("Mark Cruz", "09193456789", 14.0750, 120.6312),
        Rider("Rex Castillo", "09204567890", 14.0685, 120.6280),
        Rider("Aaron Mercado", "09215678901", 14.0720, 120.6345)
    )

    fun findNearestRider(pickupLat: Double, pickupLng: Double): Rider? {
        if (riders.isEmpty()) return null
        return riders.minByOrNull { it.distanceTo(pickupLat, pickupLng) }
    }
}