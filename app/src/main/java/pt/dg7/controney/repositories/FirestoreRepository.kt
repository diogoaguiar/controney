package pt.dg7.controney.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import pt.dg7.controney.models.Bank
import pt.dg7.controney.models.Location
import pt.dg7.controney.models.Transaction

object FirestoreRepository {
    private const val TAG = "FirestoreRepository"

    private val transactions = MutableLiveData<List<Transaction>>()
    private val bank = MutableLiveData<Bank>()

    init {
        transactions.value = listOf()

        FirebaseFirestore.getInstance().firestoreSettings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
    }

    fun getTransactions(user: FirebaseUser?): MutableLiveData<List<Transaction>> {
        FirebaseFirestore.getInstance().collection("transactions")
            .whereEqualTo("user", user?.uid)
            .orderBy("created_at", Query.Direction.DESCENDING)
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val list = ArrayList<Transaction>()
                    for (item in it.result!!) {
                        list.add(
                            Transaction(
                                item.id,
                                item.getDouble("amount")!!,
                                item.getDate("created_at")!!,
                                item.getString("type")!!,
                                item.getString("user")!!
                            )
                        )
                    }

                    transactions.value = list
                } else {
                    Log.w(TAG, "Error getting documents.", it.exception)
                }
            }

        return transactions
    }

    fun getBank(user: FirebaseUser?): MutableLiveData<Bank> {
        FirebaseFirestore.getInstance().collection("banks")
            .whereEqualTo("user", user?.uid)
            .whereEqualTo("name", "default")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "Banks loaded")
                    val qds = it.result?.first()!!
                    bank.value = Bank(
                        qds.id,
                        qds.getString("name")!!,
                        qds.getDouble("balance")!!,
                        qds.getString("currency")!!,
                        Location(qds.getGeoPoint("location")!!),
                        qds.getString("user")!!
                    )
                } else {
                    Log.w(TAG, "Error getting documents.", it.exception)
                }
            }

        return bank
    }

    fun setBalance(bank: Bank, balance: Double): Task<Void> {
        val data = bank.hashMap()
        data["balance"] = balance

        return FirebaseFirestore.getInstance().collection("banks")
            .document(bank.id)
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "Balance updated")
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to update balance")
            }
    }

    fun addTransaction(bank: Bank, transaction: Transaction): Task<Void> {
        val data = transaction.hashMap()
        data["bank"] = FirebaseFirestore.getInstance().collection("banks").document(bank.id)

        return FirebaseFirestore.getInstance().collection("transactions")
            .document()
            .set(data)
            .addOnSuccessListener {
                Log.d(TAG, "Transaction added")
                setBalance(bank, bank.balance + transaction.amount)
            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to add transaction")
            }
    }
}