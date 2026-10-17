class Delivery(
    private var deliveryId: String = "",
    private var deliveryOrder: DeliveryDetails,
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

        println("Order Details:")
        deliveryOrder.displayOrderInfo()

        println("Assigned Rider: $rider")
        }
    }
