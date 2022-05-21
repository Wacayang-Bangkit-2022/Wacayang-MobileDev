package com.c22_pc383.wacayang

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import com.c22_pc383.wacayang.data.AppPreference
import com.c22_pc383.wacayang.databinding.ActivityLoginBinding
import com.c22_pc383.wacayang.helper.IGeneralSetup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity(), IGeneralSetup {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setup()
    }

    override fun onResume() {
        super.onResume()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    override fun setup() {
        auth = Firebase.auth
        setupGoogleSignIn()
        setupAnonymousSignIn()
    }

    override fun enableControl(isEnabled: Boolean) {
        binding.progressBar.isVisible = !isEnabled
        binding.guestSignIn.isEnabled = isEnabled
        binding.googleSignIn.isEnabled = isEnabled
    }

    // region Google Sign In
    private fun setupGoogleSignIn() {
        // Default web client ID will be generated once the app built
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        val gsc = GoogleSignIn.getClient(this, gso)

        binding.googleSignIn.setOnClickListener {
            enableControl(false)
            launchGoogleSignIn.launch(gsc.signInIntent)
        }
    }

    private val launchGoogleSignIn = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val response = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val user = response.getResult(ApiException::class.java)!!
                authWithGoogle(user.idToken!!)
            } catch (e: ApiException) {
                onFailed()
            }
        }
    }

    private fun authWithGoogle(token: String) {
        auth.signInWithCredential(GoogleAuthProvider.getCredential(token, null))
            .addOnCompleteListener(this) { response ->
                if (response.isSuccessful) {
                    updateSignInData(auth.currentUser)
                } else onFailed()
            }
    }
    // endregion

    // region Anonymous Sign In
    private fun setupAnonymousSignIn() {
        binding.guestSignIn.setOnClickListener {
            enableControl(false)
            authAnonymously()
        }
    }

    private fun authAnonymously() {
        auth.signInAnonymously()
            .addOnCompleteListener(this) { response ->
                if (response.isSuccessful) {
                    updateSignInData(auth.currentUser)
                } else onFailed()
            }
    }
    // endregion

    private fun updateSignInData(user: FirebaseUser?) {
        user?.getIdToken(true)?.apply {
            addOnSuccessListener { result ->
                AppPreference(this@LoginActivity).setToken(result.token!!)
                onSuccess()
            }
            addOnFailureListener { _ -> onFailed() }
        }
    }

    private fun onSuccess() {
        Log.d("Hello", AppPreference(this).getToken())
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun onFailed() {
        enableControl(true)
        Toast.makeText(this, getString(R.string.sign_in_failed), Toast.LENGTH_SHORT).show()
    }
}