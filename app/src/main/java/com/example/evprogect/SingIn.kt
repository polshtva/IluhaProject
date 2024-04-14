package com.example.evprogect

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.evprogect.databinding.ActivitySingInBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SingIn : AppCompatActivity() {
    lateinit var binding : ActivitySingInBinding
    lateinit var sharedPreferences: SharedPreferences
    private val database = FirebaseDatabase.getInstance().reference.child("users")
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySingInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var arrEdit = arrayOf(binding.editEmail, binding.editPassword)
        for (a in arrEdit){
            a.addTextChangedListener {
                changeStatusBtn()
            }
        }

        firebaseAuth = FirebaseAuth.getInstance()

        binding.google.setOnClickListener {

//            googleSignIn()
        }

        binding.btnSingIn.setOnClickListener {
            if (binding.editEmail.text.toString().isNotEmpty() && binding.editPassword.text.toString().isNotEmpty()){
                if (isEmailValid(binding.editEmail.text.toString())){
//                    val mAuth = FirebaseAuth.getInstance()
                    val email = binding.editEmail.text.toString()
                    val password = binding.editPassword.text.toString()

                    database.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var userExists = false
                            for (snapshot in dataSnapshot.children) {
                                val user = snapshot.getValue(User::class.java)
                                if (user?.login == email && user?.password == password) {
                                    userExists = true

                                    // Проверяем роль пользователя
                                    if (user?.role == "admin") {
                                        val intent = Intent(this@SingIn, crEv::class.java)
                                        intent.putExtra("userId", user.id)
                                        startActivity(intent)
                                    } else {
                                        // Если роль пользователя не админ, переходим на Home
                                        val intent = Intent(this@SingIn, Home::class.java)
                                        // Передаем ID пользователя в следующую активити
                                        intent.putExtra("userId", user.id)
                                        startActivity(intent)
                                    }
                                    break
                                }
                            }
                            if (!userExists) {
                                Toast.makeText(this@SingIn, "Пользователь не найден", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(this@SingIn, "Database operation cancelled: ${error.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                else{
                    Toast.makeText(this, "Не корректно введена почта", Toast.LENGTH_LONG).show()
                }
            }
            else{
                Toast.makeText(this, "Введите значение", Toast.LENGTH_LONG).show()
            }
        }

        binding.linkForgotPassword.setOnClickListener {
            changeActivity(ForgotPassword::class.java)
        }

        binding.linkSingUp.setOnClickListener {
            changeActivity(SingUp::class.java)
        }
        binding.viewPassword.setOnClickListener {
            viewPassword(binding.editPassword)
        }
    }

    private fun changeStatusBtn() {
        binding.btnSingIn.isEnabled =
            binding.editEmail.text.toString().isNotEmpty() && binding.editPassword.text.toString().isNotEmpty()
    }

    private fun changeActivity(activity: Class<out Activity>) {
        val intent = Intent(this, activity)
        startActivity(intent)
    }

    private fun isEmailValid(email : String) : Boolean{
        return CommonUtils.isEmailValid(email)
    }
    private fun viewPassword(textPas : EditText){
        return CommonUtils.viewPassword(textPas)
    }
//    private fun googleSignIn() {
//        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken(getString(R.string.default_web_client_id))
//            .requestEmail()
//            .build()
//
//        val googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
//        val signInIntent = googleSignInClient.signInIntent
//        startActivityForResult(signInIntent, RC_SIGN_IN)
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == RC_SIGN_IN) {
//            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
//            try {
//                val account = task.getResult(ApiException::class.java)
//                firebaseAuthWithGoogle(account)
//            } catch (e: ApiException) {
//                Log.w(TAG, "Google sign in failed", e)
//                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//
//    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
//        val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val user = firebaseAuth.currentUser
//                } else {
//                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
//                }
//            }
//    }
//
//    companion object {
//        private const val RC_SIGN_IN = 9001
//    }
}

