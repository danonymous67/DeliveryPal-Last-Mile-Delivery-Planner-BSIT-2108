abstract class User(
    protected var name: String,
    protected var phone: String
){
    fun displayInfo() {
        println("Name: $name")
        println("Phone: $phone")
    }
    fun setInfo(name:String, phone:String){
        this.name = name
        this.phone = phone
    }
}