package pt.dg7.controney

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import pt.dg7.controney.adapters.TransactionsRecyclerViewAdapter
import pt.dg7.controney.viewmodels.MainViewModel
import java.text.NumberFormat
import java.util.Locale


class MainActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "MainActivity"
        private const val RC_SIGN_IN = 100
        private const val RC_NEW_TRANSACTION = 101
        private const val PARAM_SIGNING_IN_STATUS = "signing_in"
    }

    private val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
    private var signingIn = false

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState !== null) {
            signingIn = savedInstanceState.getBoolean(PARAM_SIGNING_IN_STATUS, signingIn)
        }

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.run {
            putBoolean(PARAM_SIGNING_IN_STATUS, signingIn)
        }
        super.onSaveInstanceState(outState)
    }

    fun init() {
        val transactions = viewModel.transactions.value
        val adapter = TransactionsRecyclerViewAdapter(
            this,
            if (transactions !== null) transactions else listOf()
        )
        rv_transactions.adapter = adapter
        rv_transactions.layoutManager = LinearLayoutManager(this)

        val balance = viewModel.bank.value?.balance
        tv_balance.text = NumberFormat.getCurrencyInstance(Locale("pt", "PT"))
            .format(if (balance !== null) balance else 0.0)

        viewModel.transactions.observe(this, Observer {
            Log.d(TAG, "Transactions updated")
            adapter.transactions = it
            adapter.notifyDataSetChanged()
        })
        viewModel.bank.observe(this, Observer {
            Log.d(TAG, "Balance updated")
            tv_balance.text = NumberFormat.getCurrencyInstance(Locale("pt", "PT"))
                .format(it.balance)
        })

        fab_add.setOnClickListener {
            val intent = Intent(this, NewTransactionActivity::class.java)
            intent.putExtra("userId", viewModel.user.value?.uid)
            intent.putExtra("bankId", viewModel.bank.value?.id)

            startActivityForResult(intent, RC_NEW_TRANSACTION)
        }
    }

    override fun onStart() {
        super.onStart()

        if (viewModel.user.value === null && !signingIn) {
            signIn()
        }
    }

    private fun signIn() {
        signingIn = true
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .build(),
            RC_SIGN_IN
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when(requestCode) {
            RC_SIGN_IN -> {
                signingIn = false
                if (resultCode == Activity.RESULT_OK) {
                    // Successfully signed in
                    val user = FirebaseAuth.getInstance().currentUser

                    Log.d(TAG, "Login successful with user ${user?.email} (${user?.uid})")

                    viewModel.setUser(user)
                    Toast.makeText(baseContext, "Sign in successful.", Toast.LENGTH_SHORT).show()
                    viewModel.getBank()
                    viewModel.getTransactions()

                    init()
                } else {
                    // Sign in failed. If response is null the user canceled the
                    // sign-in flow using the back button. Otherwise check
                    // response.getError().getErrorCode() and handle the error.
                    Log.d(TAG, "Login failed")

                    val homeIntent = Intent(Intent.ACTION_MAIN)
                    homeIntent.addCategory(Intent.CATEGORY_HOME)
                    homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                    startActivity(homeIntent)
                }
            }
            RC_NEW_TRANSACTION -> {
                if (resultCode == Activity.RESULT_OK) {
                    viewModel.getBank()
                    viewModel.getTransactions()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        R.id.action_recalculate_balance -> {
            viewModel.recalculateBalance()
            true
        }
        R.id.action_refresh -> {
            viewModel.getBank()
            viewModel.getTransactions()
            true
        }
        R.id.action_logout -> {
            AuthUI.getInstance().signOut(this)
                .addOnSuccessListener {
                    viewModel.setUser(null)
                    recreate()
                }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }
}
