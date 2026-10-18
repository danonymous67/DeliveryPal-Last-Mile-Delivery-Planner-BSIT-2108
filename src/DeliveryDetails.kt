class DeliveryDetails(
    private var orderId: String = "",
    private var sender: Sender,
    private var recipient: Recipient,
    private var packageDetails: PackageDetails,
    private var priorityLevel: Recipient
) {
    fun assignOrderID(orderId: String) {
        this.orderId = orderId
    }
    fun displayOrderInfo() {
        println("Order ID: $orderId")
        println("Priority:")
        recipient.getPriorityLevel()
        println("Sender:")
        sender.displayInfo()
        sender.getSenderAddress()
        println("Recipient:")
        recipient.displayInfo()
        recipient.getRecipientAddress()
        println("Package:")
        packageDetails.displayPackageInfo()
    }
}