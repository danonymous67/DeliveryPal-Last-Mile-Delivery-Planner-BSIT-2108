class Rider (name: String, phone : String)
    : Person(name, phone){
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0

    fun updateCoordinates(newLat: Double, newLong: Double) {
        if(newLat >= -90.0 && newLat <= 90.0 && newLong >= -180.0 && newLong <= 180.0){
            longitude = newLong
            latitude = newLat
        }
    }
    fun displayCoordinates(){
        println("Latitude: $latitude")
        println("Longitude: $longitude")

    }
}
