package com.example.evprogect

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.evprogect.databinding.ActivityEvBinding
import com.google.firebase.database.FirebaseDatabase

class EvActivity : AppCompatActivity() {
    lateinit var binding: ActivityEvBinding
    private var isSaved = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.icBack.setOnClickListener {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
        }

        val eventId = intent.getStringExtra("eventId")

        if (eventId != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("events").child(eventId)
            databaseReference.get().addOnSuccessListener { dataSnapshot ->
                if (dataSnapshot.exists()) {
                    val title = dataSnapshot.child("name").value.toString()
                    val textInfo = dataSnapshot.child("text").value.toString()
                    val textCity = dataSnapshot.child("city").value.toString()
                    val textDate = dataSnapshot.child("date").value.toString()
                    val textTime = dataSnapshot.child("time").value.toString()
                    val textPrice = dataSnapshot.child("price").value.toString()
                    val textType = dataSnapshot.child("type").value.toString()
                    val imgResId = intent.getIntExtra("urlImg", 0)
                    isSaved = dataSnapshot.child("isSaved").getValue(Boolean::class.java) ?: false

                    if (isSaved) {
                        binding.buttonSave.setBackgroundResource(R.drawable.sohr_checked)
                    } else {
                        binding.buttonSave.setBackgroundResource(R.drawable.sohr_bl)
                    }

                    val imageView: ImageView = findViewById(R.id.imgData)

                    Glide.with(this)
                        .load(imgResId)
                        .into(imageView)

                    binding.type.text = textType
                    binding.name.text = title
                    binding.time.text = textTime
                    binding.date.text = textDate
                    binding.city.text = textCity
                    binding.text.text = textInfo

                    binding.btnBuy.text = "Купить билет " + textPrice +"₽"

                    binding.btnBuy.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/?ysclid=luz9y3sidr196649540"))
                        startActivity(intent)
                    }

                    binding.buttonSave.setOnClickListener {
                        isSaved = !isSaved // Инвертируем текущее состояние
                        if (isSaved) {
                            binding.buttonSave.setBackgroundResource(R.drawable.sohr_checked)
                        } else {
                            binding.buttonSave.setBackgroundResource(R.drawable.sohr_bl)
                        }
                        databaseReference.child("isSaved").setValue(isSaved)
                    }

                } else {
                    Toast.makeText(this, "Данные не переданы", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "Ошибка при получении данных из базы данных", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Ошибка.EventsId равен null", Toast.LENGTH_SHORT).show()
        }
    }
}
