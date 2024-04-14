package com.example.evprogect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.evprogect.databinding.ActivityForgotPasswordBinding
import com.google.firebase.database.*

class ForgotPassword : AppCompatActivity() {
    lateinit var binding: ActivityForgotPasswordBinding
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Получение ссылки на базу данных Firebase
        database = FirebaseDatabase.getInstance().reference.child("users")


        binding.linkSingUp.setOnClickListener {
            val intent = Intent(this, SingUp::class.java)
            startActivity(intent)
        }


        // Обработка нажатия на кнопку "Отправить"
        binding.btnSingUp.setOnClickListener {
            val email = binding.editTextEmailForgot.text.toString().trim()
            // Проверка на наличие введенного email
            if (email.isNotEmpty()) {
                // Проверка в базе данных наличия пользователя с указанным email
                database.orderByChild("login").equalTo(email)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            // Если есть пользователь с указанным email
                            if (snapshot.exists()) {
                                // Переход к активности OTP кода и передача email
                                val intent = Intent(this@ForgotPassword, OptCode::class.java)
                                intent.putExtra("email", email)
                                startActivity(intent)
                            } else {
                                // Если пользователя с указанным email нет
                                Toast.makeText(
                                    this@ForgotPassword,
                                    "Пользователя с такой почтой нет",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Обработка ошибки при чтении из базы данных
                            Toast.makeText(
                                this@ForgotPassword,
                                "Ошибка: ${error.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            } else {
                // Вывод сообщения о необходимости ввести email
                Toast.makeText(this@ForgotPassword, "Введите почту", Toast.LENGTH_SHORT).show()
            }
        }
    }
    }
