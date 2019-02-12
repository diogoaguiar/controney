package pt.dg7.controney.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import pt.dg7.controney.models.Bank
import pt.dg7.controney.models.Transaction
import pt.dg7.controney.repositories.FirestoreRepository
import java.util.*

class NewTransactionViewModel : ViewModel() {
    private val _amount = MutableLiveData<Double>()
    val amount: LiveData<Double>
        get() = _amount

    private val _description = MutableLiveData<String>()
    val description: LiveData<String?>
        get() = _description

    private val user = FirebaseAuth.getInstance().currentUser
    private val _bank = FirestoreRepository.getBank(user)
    val bank: LiveData<Bank>
        get() = _bank

    val _type = MutableLiveData<String>()
    val type: LiveData<String>
        get() = _type

    init {
        _amount.value = 0.0
    }

    fun setAmount(value: Double) {
        _amount.value = value
    }

    fun setType(type: CharSequence?) {
        _type.value = type?.toString()
    }

    fun confirm(): Task<Void> {
        val transaction = Transaction(
            null,
            _amount.value!!,
            Date(),
            _type.value?.toLowerCase() ?: "withdraw",
            user!!.uid,
            _description.value
        )

        return FirestoreRepository.addTransaction(bank.value!!, transaction)
    }

    fun setDescription(description: String?) {
        _description.value = description
    }
}