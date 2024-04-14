package com.example.evprogect

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.example.evprogect.databinding.ActivitySingUpBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class SingUp : AppCompatActivity() {
    lateinit var  binding: ActivitySingUpBinding
    private val database = FirebaseDatabase.getInstance().reference.child("users")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        binding = ActivitySingUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var arrEdit = arrayOf(binding.editEmail, binding.editName, binding.editPassword, binding.editPasswordSecond)
        for (a in arrEdit){
            a.addTextChangedListener {
                changeStatusBtn()
            }
        }

        binding.btnSingIn.setOnClickListener {
            val email = binding.editEmail.text.toString()
            val name = binding.editName.text.toString()
            val password = binding.editPassword.text.toString()
            val confirmPassword = binding.editPasswordSecond.text.toString()

            if (email.isNotEmpty() && name.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                if (isEmailValid(email)) {
                    if (validPassword(password) && validPassword(confirmPassword)) {
                        if (password == confirmPassword) {
                            // Проверяем, существует ли пользователь с таким логином
                            database.orderByChild("login").equalTo(email).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // Пользователь с таким логином уже существует
                                        Toast.makeText(this@SingUp, "Пользователь с таким логином уже зарегистрирован", Toast.LENGTH_LONG).show()
                                    } else {
                                        // Пользователь с таким логином не существует, можно продолжить регистрацию
                                        val user = User(name = name, login = email, password = password, darkModeEnabled = false)
                                        val newUserRef = database.push()
                                        val userId = newUserRef.key // Получаем ключ
                                        user.id = userId ?: "" // Устанавливаем ключ как id пользователя
                                        newUserRef.setValue(user)
                                        changeActivity(SingIn::class.java)
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Ошибка при чтении из базы данных
                                    Toast.makeText(this@SingUp, "Ошибка при чтении из базы данных", Toast.LENGTH_LONG).show()
                                }
                            })
                        } else {
                            Toast.makeText(this, "Пароли не совпадают", Toast.LENGTH_LONG).show()
                        }
                    } else {
                        Toast.makeText(this, "Пароль должен содержать не менее 8 символов...", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Не корректно введена почта", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "Введите значение", Toast.LENGTH_LONG).show()
            }
        }



        binding.linkSingUp.setOnClickListener {
            changeActivity(SingIn::class.java)
        }

        binding.viewPassword1.setOnClickListener {
            viewPassword(binding.editPassword)
        }

        binding.viewPassword2.setOnClickListener {
            viewPassword(binding.editPasswordSecond)
        }
    }

    private fun changeStatusBtn() {
        binding.btnSingIn.isEnabled =
            binding.editEmail.text.toString().isNotEmpty()
                    && binding.editName.text.toString().isNotEmpty()
                    && binding.editPassword.text.toString().isNotEmpty()
                    && binding.editPasswordSecond.text.toString().isNotEmpty()
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

    private fun validPassword(pass : String) : Boolean{
        return CommonUtils.validPassword(pass)
    }
}
