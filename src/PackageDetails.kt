class PackageDetails(
    private var description: String,
    private var weight: Double
) {
    fun displayPackageInfo() {
        println("Package: $description")
        println("Weight: $weight kg")
    }

    fun getWeight(): Double {
        return weight
    }
}