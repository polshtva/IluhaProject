package com.example.evprogect

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.evprogect.databinding.ActivityOptCodeBinding

class OptCode : AppCompatActivity() {
    lateinit var binding: ActivityOptCodeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptCodeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailGet = intent.getStringExtra("email")

        binding.buttonLogIntoHome.setOnClickListener {
            if (getEnteredOTP() == "222222") {
                val intent = Intent(this, NewPassword::class.java)
                intent.putExtra("email", emailGet)
                startActivity(intent)
            }
        }
    }
        private fun getEnteredOTP(): String {
            // Получите введенные пользователем значения OTP кода из EditText
            val otpStringBuilder = StringBuilder()
            otpStringBuilder.append(binding.editTextTextPersonName1.text.toString())
            otpStringBuilder.append(binding.editTextTextPersonName2.text.toString())
            otpStringBuilder.append(binding.editTextTextPersonName3.text.toString())
            otpStringBuilder.append(binding.editTextTextPersonName4.text.toString())
            otpStringBuilder.append(binding.editTextTextPersonName5.text.toString())
            otpStringBuilder.append(binding.editTextTextPersonName6.text.toString())
            return otpStringBuilder.toString()
        }
}