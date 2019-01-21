package pt.dg7.controney.models

import com.google.firebase.firestore.GeoPoint

class Location(var latitude : Double = 0.0,
               var longitude : Double = 0.0) {
    constructor(geoPoint: GeoPoint) : this(geoPoint.latitude, geoPoint.longitude)
}