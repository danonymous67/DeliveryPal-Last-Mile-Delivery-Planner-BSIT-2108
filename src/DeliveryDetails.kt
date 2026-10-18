class DeliveryDetails(
    private var orderId: String = "",
    private var sender: Sender,
    private var recipient: Recipient,
    private var packageDetails: PackageDetails
) {
    fun assignOrderID(orderId: String) {
        this.orderId = orderId
    }
    fun displayOrderInfo() {
        println("Order ID: $orderId")
        println("Priority:")
        print(recipient.getPriorityLevel())
        println("Sender:")
        sender.displayInfo()
        print(sender.getSenderAddress())
        println("Recipient:")
        recipient.displayInfo()
        println(recipient.getRecipientAddress())
        println("Package:")
        packageDetails.displayPackageInfo()
    }
}