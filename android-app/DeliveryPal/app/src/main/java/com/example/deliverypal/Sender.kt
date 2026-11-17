package com.example.deliverypal

class Sender(
    name: String,
    phone: String
) : User(name, phone) {
    var address: Address = Address()

    fun setSenderAddress(
        street: String,
        baranggay: String,
        landmark: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ) {
        address.setAddress(street, baranggay, landmark, latitude, longitude)
    }

    fun getSenderAddress(): String = address.getAddress()

    fun createPackageDetails(
        description: String,
        weight: Double,
        quantity: Int
    ): PackageDetails {
        return PackageDetails(description, weight, quantity)
    }

    fun createDeliveryRequest(
        orderId: String,
        recipientName: String,
        recipientPhone: String,
        street: String,
        baranggay: String,
        landmark: String,
        description: String,
        weight: Double,
        quantity: Int,
        priority: String,
        pickupLat: Double = 0.0,
        pickupLng: Double = 0.0,
        destLat: Double = 0.0,
        destLng: Double = 0.0
    ): Delivery {
        val recipient = Recipient(recipientName, recipientPhone)
        recipient.setRecipientAddress(street, baranggay, landmark, destLat, destLng)
        recipient.priorityLevel = priority

        val packageDetails = createPackageDetails(description, weight, quantity)

        return Delivery(
            deliveryId = orderId,
            sender = this,
            recipient = recipient,
            packageDetails = packageDetails
        )
    }
}
