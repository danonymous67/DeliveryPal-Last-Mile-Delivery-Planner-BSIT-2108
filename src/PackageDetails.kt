class PackageDetails(
    private var description: String,
    private var weight: Double,
    private var quantity: Int
) {
    fun displayPackageInfo() {
        println("Package: $description")
        println("Weight: $weight kg")
        println("Quantity: $quantity")
    }

    fun getTotalWeight(): Double {
        return weight*quantity
    }
}