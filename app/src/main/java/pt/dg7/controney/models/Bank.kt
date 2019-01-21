package pt.dg7.controney.models

import com.google.firebase.firestore.GeoPoint

class Bank(val id: String,
           val name: String = "default",
           var balance: Double = 0.0,
           val currency: String = "euro",
           val location: Location = Location(),
           val user: String
) {
    fun hashMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()

        map["name"] = name
        map["balance"] = balance
        map["currency"] = currency
        map["location"] = GeoPoint(location.latitude, location.longitude)
        map["user"] = user

        return map
    }
}