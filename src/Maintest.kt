fun main() {

    // Create sender
    val sender = Sender("Juan Dela Cruz", "09171234567")

    sender.setSenderAddress(
        "Villaviray Street",
        "Barangay 5",
        "Pickleball Court"
    )

    // Recipient coordinates
    val recipientLat = 14.0780
    val recipientLng = 120.6350

    // Create sample delivery
    val delivery = sender.createDeliveryRequest(
        orderId = "DP-001",
        recipientName = "Maria Santos",
        recipientPhone = "09991234567",
        street = "Villaviray",
        baranggay = "6",
        landmark = "Primewater Tower",
        description = "Electronics",
        weight = 2.5,
        quantity = 1,
        priority = "Normal"
    )

    // Find and assign nearest rider
    val nearestRider = RiderRepository.findNearestRider(
        recipientLat,
        recipientLng
    )

    if (nearestRider != null) {
        delivery.assignRider(nearestRider)

        println("Nearest Rider:")
        nearestRider.displayInfo()

        println(
            "Distance: ${
                "%.2f".format(
                    nearestRider.distanceTo(
                        recipientLat,
                        recipientLng
                    )
                )
            } km"
        )
    }

    // Display complete delivery information
    println("\n========== DELIVERY DETAILS ==========")
    delivery.displayDeliveryInfo()
    println("======================================")
}