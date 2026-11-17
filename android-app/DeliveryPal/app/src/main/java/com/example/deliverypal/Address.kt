package com.example.deliverypal

class Address(
    var street: String = "",
    var baranggay: String = "",
    var landmark: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0
) {
    fun getAddress(): String {
        val parts = listOf(street, baranggay, landmark).filter { it.isNotBlank() }
        return if (parts.isNotEmpty()) parts.joinToString(", ") else "No address specified"
    }

    fun setAddress(
        street: String,
        baranggay: String,
        landmark: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ) {
        this.street = street
        this.baranggay = baranggay
        this.landmark = landmark
        this.latitude = latitude
        this.longitude = longitude
    }
}
