package com.example.deliverypal

class Recipient(
    name: String,
    phone: String
) : User(name, phone) {
    var address: Address = Address()

    var priorityLevel: String = "Normal"
        set(value) {
            if (value == "Normal" || value == "Urgent" || value == "Relaxed") {
                field = value
            }
        }

    fun setRecipientAddress(
        street: String,
        baranggay: String,
        landmark: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ) {
        address.setAddress(street, baranggay, landmark, latitude, longitude)
    }

    fun getRecipientAddress(): String {
        return address.getAddress()
    }
}
