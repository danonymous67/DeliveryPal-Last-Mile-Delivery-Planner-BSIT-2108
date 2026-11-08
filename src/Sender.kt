class Sender(name: String, phone: String) : User(name, phone) {
    private var address: Address = Address()

    fun setSenderAddress(street: String, baranggay: String, landmark: String) {
        address.setAddress(street, baranggay, landmark)
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
        priority: String
    ): Delivery {

        val recipient = Recipient(recipientName, recipientPhone)
        recipient.setRecipientAddress(street, baranggay, landmark)
        recipient.setPriorityLevel(priority)

        val packageDetails = createPackageDetails(description, weight, quantity)

        return Delivery(
            deliveryId = orderId,
            sender = this,
            recipient = recipient,
            packageDetails = packageDetails
        )
    }
}