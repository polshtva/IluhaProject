package com.example.evprogect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.example.evprogect.databinding.ActivityNewPasswordBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class NewPassword : AppCompatActivity() {
    lateinit var binding : ActivityNewPasswordBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance().reference.child("users")

        val emailGet = intent.getStringExtra("email")

        binding.viewPassword.setOnClickListener {
            viewPassword(binding.editTextNewPassword)
        }

        binding.buttonLogIntoHome.setOnClickListener {
            val email = emailGet // Получаем адрес электронной почты из предыдущей активности
            val newPassword = binding.editTextNewPassword.text.toString().trim()

            if (email != null && newPassword.isNotEmpty()) {
                changePassword(email, newPassword)
            } else {
                Toast.makeText(this, "Email or password is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changePassword(email: String, newPassword: String) {
        val query = database.orderByChild("login").equalTo(email)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (userSnapshot in dataSnapshot.children) {
                        val userId = userSnapshot.key
                        if (userId != null) {
                            database.child(userId).child("password").setValue(newPassword)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(this@NewPassword, "Пароль изменен", Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this@NewPassword, SingIn::class.java)
                                        startActivity(intent)

                                    } else {
                                        Toast.makeText(this@NewPassword, "Ошибка при изменение пароля", Toast.LENGTH_SHORT).show()
                                    }
                                }
                        }
                    }
                } else {
                    Toast.makeText(this@NewPassword, "User not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@NewPassword, "Database error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun viewPassword(textPas : EditText){
        return CommonUtils.viewPassword(textPas)
    }
}
