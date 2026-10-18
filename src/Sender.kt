class Sender(name:String, phone:String)
    : User(name, phone){
    private var address: Address = Address()

    fun setSenderAddress(
        street: String,
        baranggay: String,
        landmark: String)
    {
        address.setAddress(street, baranggay, landmark)
    }
    fun getSenderAddress(): String{
        return(address.getAddress())
    }
}