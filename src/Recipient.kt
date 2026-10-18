class Recipient(name:String, phone:String)
    : Person(name, phone) {
    private var address: Address = Address()
    private var priorityLevel: String = "Normal"

    fun setRecipientAddress(
        street: String,
        baranggay: String,
        landmark: String
    ) {
        address.setAddress(street, baranggay, landmark)
    }
    fun getRecipientAddress(): String {
        return(address.getAddress())
    }
    fun setPriorityLevel(priorityLevel: String){
        if (priorityLevel == "Normal" || priorityLevel == "Urgent" || priorityLevel == "Relaxed")
            this.priorityLevel = priorityLevel
    }
    fun getPriorityLevel(){
        println(priorityLevel)
    }
}