class Recipient : Person() {
    private var address: Address = Address()
    private var priorityLevel: String = "Normal"

    fun setRecipientAddress(
        street: String,
        baranggay: String,
        landmark: String
    ) {
        address.setAddress(street, baranggay, landmark)
    }

    fun getRecipientAddress() {
        println(address.getAddress())
    }
}