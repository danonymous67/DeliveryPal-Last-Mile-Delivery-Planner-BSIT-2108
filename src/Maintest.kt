fun main(){
    val sender1 = Sender("Daniel", "09123456789")

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
        priority = "Urgent"
    )

    delivery1.displayDeliveryInfo()
}