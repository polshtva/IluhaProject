package com.example.evprogect

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.evprogect.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val handler = Handler()
        val delayMil=3000L

        handler.postDelayed({
            checkInternetConnection()
            finish()
        }, delayMil)

        binding.imageLogo.setOnClickListener {
            checkInternetConnection()
        }

    }
    private fun checkInternetConnection() {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        val isConnected: Boolean = activeNetwork != null && activeNetwork.isConnectedOrConnecting

        if (isConnected) {
            val intent = Intent(this, SingIn::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "Нет интернета", Toast.LENGTH_SHORT).show()
        }
    }

}