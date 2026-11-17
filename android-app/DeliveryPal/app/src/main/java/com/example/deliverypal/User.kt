package com.example.deliverypal

abstract class User(
    var name: String,
    var phone: String
) {
    fun displayInfo() {
        println("Name: $name")
        println("Phone: $phone")
    }

    fun setInfo(name: String, phone: String) {
        this.name = name
        this.phone = phone
    }
}
