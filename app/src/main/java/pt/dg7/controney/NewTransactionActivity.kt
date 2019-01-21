package pt.dg7.controney

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_new_transaction.*
import pt.dg7.controney.viewmodels.NewTransactionViewModel

class NewTransactionActivity : AppCompatActivity() {

    private lateinit var viewModel: NewTransactionViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_transaction)

        val userId = intent.getStringExtra("userId")
        val bankId = intent.getStringExtra("bankId")

        if (userId === null || bankId === null) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        viewModel = ViewModelProviders.of(this).get(NewTransactionViewModel::class.java)

        tv_type.text = getString(R.string.withdraw)

        b_confirm.isEnabled = false
        viewModel.bank.observe(this, Observer {
            b_confirm.isEnabled = true
        })

        et_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val amount = try {
                    s.toString().toDouble()
                } catch (nfe: NumberFormatException) {
                    0.0
                }
                viewModel.setAmount(amount)

                if (amount > 0) {
                    tv_type.text = getString(R.string.deposit)
                } else {
                    tv_type.text = getString(R.string.withdraw)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })

        b_confirm.setOnClickListener {
            viewModel.setAmount(et_amount.text.toString().toDouble())
            viewModel.confirm()
                .addOnSuccessListener {
                    setResult(Activity.RESULT_OK)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to add the transaction", Toast.LENGTH_LONG).show()
                }
        }
    }
}
