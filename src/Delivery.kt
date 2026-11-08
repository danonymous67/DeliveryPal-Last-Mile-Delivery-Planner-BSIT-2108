class Delivery(
    private var deliveryId: String,
    private var sender: Sender,
    private var recipient: Recipient,
    private var packageDetails: PackageDetails,
    private var rider: Rider? = null,
    private var status: String = "Pending"
) {

    fun assignRider(rider: Rider) {
        this.rider = rider
        status = "Assigned"
    }

    fun updateStatus(newStatus: String) {
        status = newStatus
    }

    fun assignID(newID: String) {
        deliveryId = newID
    }

    fun displayDeliveryInfo() {
        println("Delivery ID: $deliveryId")
        println("Status: $status")

        println("\nSender:")
        sender.displayInfo()
        println(sender.getSenderAddress())

        println("\nRecipient:")
        recipient.displayInfo()
        println(recipient.getRecipientAddress())

        println("\nPriority: ${recipient.getPriorityLevel()}")

        println("\nPackage")
        packageDetails.displayPackageInfo()

        println("\nAssigned Rider:")
        if (rider != null) {
            rider!!.displayInfo()
        } else {
            println("No rider assigned yet.")
        }
    }
}