package pt.dg7.controney

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_new_transaction.*
import pt.dg7.controney.viewmodels.NewTransactionViewModel

class NewTransactionActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private lateinit var viewModel: NewTransactionViewModel
    private lateinit var adapter: ArrayAdapter<CharSequence>

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

        b_confirm.isEnabled = false
        viewModel.bank.observe(this, Observer {
            b_confirm.isEnabled = true
        })

        val spinner: Spinner = findViewById(R.id.s_transaction_type)
        adapter = ArrayAdapter.createFromResource(
            this,
            R.array.transaction_types,
            android.R.layout.simple_spinner_item
        ).also {
            it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = it
        }
        spinner.setSelection(0)
        spinner.onItemSelectedListener = this

        b_confirm.setOnClickListener {
            viewModel.setAmount(et_amount.text.toString().toDouble())
            viewModel.setDescription(et_description.text?.toString())
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

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        viewModel.setType(adapter.getItem(position))
    }
}
