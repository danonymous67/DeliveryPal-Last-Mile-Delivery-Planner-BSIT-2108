class DeliveryOrder(
    private var orderId: String,
    private var sender: Sender,
    private var recipient: Recipient,
    private var packageDetails: PackageDetails,
    private var priorityLevel: String = "Normal"
) {
    fun displayOrderInfo() {
        println("Order ID: $orderId")
        println("Priority: $priorityLevel")

        println("Sender:")
        sender.displayInfo()
        sender.getSenderAddress()

        println("\nRecipient:")
        recipient.displayInfo()
        recipient.getRecipientAddress()

        println("Package:")
        packageDetails.displayPackageInfo()
    }
}