import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class GoogleSignInHelper(
    private val context: Context,
    private val auth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    fun registerForResult(
        launcher: ActivityResultLauncher<Intent>,
        onSignInSuccess: (FirebaseUser?) -> Unit,
        onSignInFailure: (String) -> Unit
    ) {
        launcher.launch(googleSignInClient.signInIntent)
    }

    fun handleActivityResult(result: ActivityResult, onSignInSuccess: (FirebaseUser?) -> Unit, onSignInFailure: (String) -> Unit) {
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("GoogleSignInHelper", "Google Sign-In result received")
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                Log.d("GoogleSignInHelper", "Google Sign-In account retrieved: ${account.email}")
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                auth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (authTask.isSuccessful) {
                        Log.d("GoogleSignInHelper", "Google Sign-In successful")
                        val currentUser: FirebaseUser? = auth.currentUser
                        currentUser?.let {
                            val userId = it.uid
                            val userEmail = it.email
                            val userData = mapOf("email" to userEmail, "authenticated" to true)
                            database.child("user").child(userId).setValue(userData)
                                .addOnCompleteListener { dbTask ->
                                    if (dbTask.isSuccessful) {
                                        Log.d("GoogleSignInHelper", "User data stored successfully in database")
                                        onSignInSuccess(currentUser)
                                    } else {
                                        Log.d("GoogleSignInHelper", "Failed to store user data: ${dbTask.exception?.message}")
                                        onSignInFailure("Failed to store user data: ${dbTask.exception?.message}")
                                    }
                                }
                        } ?: onSignInFailure("Failed to retrieve current user")
                    } else {
                        Log.d("GoogleSignInHelper", "Google Sign-In failed: ${authTask.exception?.message}")
                        onSignInFailure("Google Sign-In failed: ${authTask.exception?.message}")
                    }
                }
            } catch (e: ApiException) {
                Log.d("GoogleSignInHelper", "Google Sign-In failed: ${e.message}")
                onSignInFailure("Google Sign-In failed: ${e.message}")
            }
        } else {
            Log.d("GoogleSignInHelper", "Google Sign-In result not OK: ${result.resultCode}")
            //onSignInFailure("Google Sign-In result not OK: ${result.resultCode}")
        }
    }
}
