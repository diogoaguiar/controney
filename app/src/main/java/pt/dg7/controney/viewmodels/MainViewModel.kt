package pt.dg7.controney.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import pt.dg7.controney.models.Bank
import pt.dg7.controney.models.Transaction
import pt.dg7.controney.repositories.FirestoreRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val TAG = "MainViewModel"
    }

    private val _user = MutableLiveData<FirebaseUser>()
    val user: LiveData<FirebaseUser?>
        get() = _user

    private var _transactions = MutableLiveData<List<Transaction>>()
    val transactions: LiveData<List<Transaction>>
        get() = _transactions

    private var _bank = MutableLiveData<Bank>()
    val bank: LiveData<Bank>
        get() = _bank

    init {
        _transactions.value = listOf()
    }

    fun setUser(user: FirebaseUser?) {
        _user.value = user
    }

    fun getTransactions() {
        _transactions = FirestoreRepository.getTransactions(_user.value)
    }

    fun getBank() {
        _bank = FirestoreRepository.getBank(_user.value)
    }

    fun recalculateBalance() {
        var balance = 0.0
        _transactions.value?.forEach {
            balance += it.amount
        }

        _bank.value?.balance = balance
        FirestoreRepository.setBalance(bank.value!!, balance)
            .addOnSuccessListener {
                getBank()
                getTransactions()
            }
    }
}