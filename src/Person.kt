abstract class Person(
    protected var name: String = "",
    protected var phone: String = ""
) {
    fun displayInfo() {
        println("Name: $name")
        println("Phone: $phone")
    }
}