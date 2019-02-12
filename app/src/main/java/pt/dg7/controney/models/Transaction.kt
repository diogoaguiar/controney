package pt.dg7.controney.models

import java.util.*
import kotlin.collections.HashMap

class Transaction(val id: String?,
                  var amount: Double,
                  var date: Date,
                  var type: String,
                  val user: String,
                  val description: String?) {

    fun hashMap(): HashMap<String, Any> {
        val map = HashMap<String, Any>()

        if (id !== null) {
            map["id"] = id
        }
        map["amount"] = amount
        map["created_at"] = date
        map["type"] = type
        map["user"] = user
        if (description !== null) {
            map["description"] = description
        }

        return map
    }
}