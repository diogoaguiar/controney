package pt.dg7.controney.models

import com.google.firebase.firestore.QueryDocumentSnapshot

class Bank(var name : String? = null,
           var currency : String = "euro",
           var location : Location = Location()
) {
    fun set(data: QueryDocumentSnapshot?) {
        if (data === null) return

        name = data.get("name") as String
        currency = data.get("currency") as String
        location.set(data.getGeoPoint("location"))
    }
}