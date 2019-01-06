package pt.dg7.controney.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*
import pt.dg7.controney.R

class MainActivity : AppCompatActivity() {
    companion object {
        const val RC_SIGN_IN = 100
    }

    private val providers = arrayListOf(AuthUI.IdpConfig.EmailBuilder().build())
    var user : FirebaseUser? = null
    var signingIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()

        if (user === null && signingIn === false) {
            signIn()
        }
    }

    fun signIn() {
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
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser
                val userInstance = user

                if (userInstance !== null) {
                    tv_uid.text = userInstance.uid
                    tv_name.text = userInstance.displayName
                    tv_email.text = userInstance.email
                }

                Toast.makeText(baseContext, "Sign in successful.", Toast.LENGTH_SHORT).show()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(baseContext, "Sign in failed.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
