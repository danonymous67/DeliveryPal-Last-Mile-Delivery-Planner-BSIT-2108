package com.example.deliverypal

fun main() {
    val sender1 = Sender("Daniel", "09123456789")

    val riders = RiderRepository.riders

    val delivery1 = sender1.createDeliveryRequest(
        orderId = "DEL001",
        recipientName = "John",
        recipientPhone = "09987654321",
        street = "123 Main Street",
        baranggay = "Barangay 1",
        landmark = "Near the church",
        description = "Documents",
        weight = 0.5,
        quantity = 1,
        priority = "Urgent",
        pickupLat = 14.0722,
        pickupLng = 120.6306,
        destLat = 14.0780,
        destLng = 120.6350
    )

    val nearestRider = RiderRepository.findNearestRider(14.0722, 120.6306)
    if (nearestRider != null) {
        delivery1.assignRider(nearestRider)
    }

    delivery1.displayDeliveryInfo()
}
