class Sender(name:String, phone:String)
    : Person(name, phone) {
    private var address: Address = Address()

    fun setSenderAddress(
        street: String,
        baranggay: String,
        landmark: String)
    {
        address.setAddress(street, baranggay, landmark)
    }
    fun getSenderAddress(){
        println(address.getAddress())
    }
}