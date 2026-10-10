class Address {
    private var street: String = ""
    private var baranggay: String = ""
    private var landmark: String = ""

    fun getAddress(): String {
        return ("$street, $baranggay, $landmark")
    }

    fun setAddress(street: String, baranggay: String, landmark: String) {
        this.street = street
        this.baranggay = baranggay
        this.landmark = landmark
    }
}
