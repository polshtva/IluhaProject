package com.example.evprogect


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.evprogect.databinding.ActivityHomeBinding
import androidx.lifecycle.ViewModel



class Home : AppCompatActivity() {
    lateinit var binding: ActivityHomeBinding
    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val controller = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)?.findNavController()
        binding.bottomNavigationView.setupWithNavController(controller!!)

        userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        // Получаем ID пользователя из Intent и передаем его в UserViewModel
        val userId = intent.getStringExtra("userId")
        if (userId != null) {
            userViewModel.userId = userId
        }
    }
}

class UserViewModel : ViewModel() {
    lateinit var userId: String // Обязательное поле
}

