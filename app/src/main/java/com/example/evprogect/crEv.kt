package com.example.evprogect

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.evprogect.databinding.ActivityCrEvBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class crEv : AppCompatActivity() {
    lateinit var binding: ActivityCrEvBinding
    private lateinit var mDatabase: DatabaseReference
    private lateinit var mProgressDialog: ProgressDialog
    private var mImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCrEvBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDatabase = FirebaseDatabase.getInstance().reference.child("events")
        mProgressDialog = ProgressDialog(this)

        binding.addPhoto.setOnClickListener {
            val galleryIntent = Intent()
            galleryIntent.type = "image/*"
            galleryIntent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(galleryIntent, "Выберите изображение"),
                GALLERY_PICK
            )
        }

        // Устанавливаем адаптер для Spinner
        val adapter = ArrayAdapter.createFromResource(
            this,
            R.array.type_options,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter

        // Обработка нажатия кнопки создания мероприятия
        binding.buttonCreateEvent.setOnClickListener {
            startCreatingEvent()
        }
    }

    private fun startCreatingEvent() {
        val idEv = binding.editIdEv.text.toString().trim()
        val eventName = binding.editTextEventName.text.toString().trim()
        val eventDescription = binding.editTextEventDescription.text.toString().trim()
        val eventTime = binding.editTextEventTime.text.toString().trim()
        val eventDate = binding.editTextEventDate.text.toString().trim()
        val price = binding.editTextPrice.text.toString().trim()
        val eventAddress = binding.editTextEventAddress.text.toString().trim()
        val type = binding.spinnerGender.selectedItem.toString()

        // Проверяем, что все поля заполнены и выбрано изображение
        if (!TextUtils.isEmpty(idEv) && !TextUtils.isEmpty(eventName) && !TextUtils.isEmpty(eventDescription) &&
            !TextUtils.isEmpty(eventTime) && !TextUtils.isEmpty(eventDate) && !TextUtils.isEmpty(price) &&
            !TextUtils.isEmpty(eventAddress) && mImageUri != null
        ) {
            mProgressDialog.setMessage("Создание мероприятия...")
            mProgressDialog.show()

            // Создаем хэшмап с данными о мероприятии
            val eventMap = hashMapOf(
                "id" to idEv,
                "name" to eventName,
                "text" to eventDescription,
                "time" to eventTime,
                "date" to eventDate,
                "price" to price,
                "city" to eventAddress,
                "type" to type,
                "status" to "новый",
            )

            // Записываем данные в базу данных Firebase
            mDatabase.child(idEv).setValue(eventMap).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    mProgressDialog.dismiss()
                    Toast.makeText(this@crEv, "Мероприятие создано успешно", Toast.LENGTH_SHORT).show()
                    // Здесь вы можете отправить уведомление админу, используя Firebase Cloud Messaging
                } else {
                    mProgressDialog.dismiss()
                    Toast.makeText(this@crEv, "Ошибка: " + task.exception?.message, Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Заполните все поля и добавьте изображение", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GALLERY_PICK && resultCode == RESULT_OK) {
            mImageUri = data?.data
            binding.addPhoto.setImageURI(mImageUri)
        }
    }

    companion object {
        private const val GALLERY_PICK = 1
    }
}
