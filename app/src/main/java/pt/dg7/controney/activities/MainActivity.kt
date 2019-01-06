package pt.dg7.controney.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import pt.dg7.controney.R
import pt.dg7.controney.TransactionFragment
import pt.dg7.controney.dummy.DummyContent
import pt.dg7.controney.models.Bank

class MainActivity : AppCompatActivity(), TransactionFragment.OnListFragmentInteractionListener {
    companion object {
        const val TAG = "MainActivity"
        const val RC_SIGN_IN = 100
    }

    private val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
    private var signingIn = false
    var user : FirebaseUser? = null
    private val db = FirebaseFirestore.getInstance()
    private var bank = Bank()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val settings = FirebaseFirestoreSettings.Builder()
            .setTimestampsInSnapshotsEnabled(true)
            .build()
        db.firestoreSettings = settings
    }

    override fun onStart() {
        super.onStart()

        if (user === null && !signingIn) {
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
            RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        signingIn = false

        if (requestCode == RC_SIGN_IN) {
            //val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser
                Toast.makeText(baseContext, "Sign in successful.", Toast.LENGTH_SHORT).show()
                loadData()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.

                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                startActivity(homeIntent)
            }
        }
    }

    private fun loadData() {
        // Bank
        db.collection("banks")
            .whereEqualTo("user", user!!.uid)
            .whereEqualTo("name", "default")
            .get()
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    bank.set(it.result?.first())
                } else {
                    Log.w(TAG, "Error getting documents.", it.exception)
                }
            }

    }

    override fun onListFragmentInteraction(item: DummyContent.DummyItem?) {
        // Do Nothing
    }
}
