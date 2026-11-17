package com.example.deliverypal

class PackageDetails(
    var description: String,
    var weight: Double,
    var quantity: Int
) {
    fun displayPackageInfo() {
        println("Package: $description")
        println("Weight: $weight kg")
        println("Quantity: $quantity")
    }

    fun getTotalWeight(): Double {
        return weight * quantity
    }
}
