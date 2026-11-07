class Address {
    private var street: String = ""
    private var barangay: String = ""
    private var landmark: String = ""

    fun getAddress(): String {
        return ("$street, $barangay, $landmark")
    }

    fun setAddress(street: String, barangay: String, landmark: String) {
        this.street = street
        this.baranggay = barangay
        this.landmark = landmark
    }
}