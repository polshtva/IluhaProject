package com.example.evprogect

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.evprogect.databinding.ActivityCreateEventsBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.*

class CreateEventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCreateEventsBinding
    private val database = FirebaseDatabase.getInstance().reference.child("events")

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val REQUEST_IMAGE_PICK = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // обработчик клика выбора фотографии
        binding.addPhoto.setOnClickListener { showImagePickerDialog() }

        // обработчик клика при нажатие кнопки создания мероприятия
        binding.buttonCreateEvent.setOnClickListener {
            val eventName = binding.editTextEventName.text.toString()
            val eventDescription = binding.editTextEventDescription.text.toString()
            val eventTime = binding.editTextEventTime.text.toString()
            val eventDate = binding.editTextEventDate.text.toString()

            //проверка пустые значения или нет
            if (eventName.isNotBlank() && eventDescription.isNotBlank() && eventTime.isNotBlank() && eventDate.isNotBlank()) {
                uploadEventData()
            } else {
                Toast.makeText(this, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //открытие окна выбора источника фоторгафий
    private fun showImagePickerDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Выберите источник фотографии")
            .setItems(arrayOf("Галерея", "Камера")) { _, which ->
                when (which) {
                    0 -> pickImageFromGallery()
                    1 -> takePhoto()
                }
            }
        builder.show()
    }

    // выгрузка фотографий из галереи
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    // сделать фото
    private fun takePhoto() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    // выгрузка фотографии на место binding.addPhoto после закртиыя приложения и открытия активити
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let { uri ->
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        binding.addPhoto.setImageBitmap(bitmap)
                    }
                }
                REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        binding.addPhoto.setImageBitmap(it)
                    }
                }
            }
        }
    }

    // добавление данных в Firebase
    private fun uploadEventData() {
        val eventName = binding.editTextEventName.text.toString()
        val eventDescription = binding.editTextEventDescription.text.toString()
        val eventTime = binding.editTextEventTime.text.toString()
        val eventDate = binding.editTextEventDate.text.toString()
        val photoUrl = saveImageToFirebase()

        val eventId = database.push().key ?: ""
        val event = Events(eventId, eventName, eventDescription, photoUrl, eventTime, eventDate)

        database.child(eventId).setValue(event)
            .addOnSuccessListener {
                Toast.makeText(this, "Данные успешно добавлены", Toast.LENGTH_SHORT).show()
                sendNotification("Новое мероприятие: $eventName")
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Ошибка в добавлении данных: $exception", Toast.LENGTH_SHORT).show()
            }
    }

    // сохранение фото в Firebase
    private fun saveImageToFirebase(): String {
        val storageRef = FirebaseStorage.getInstance().reference
        val photoRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
        val baos = ByteArrayOutputStream()
        (binding.addPhoto.drawable).apply {
            if (this != null && this is BitmapDrawable) {
                this.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val imageData = baos.toByteArray()
                photoRef.putBytes(imageData)
            }
        }
        return photoRef.toString()
    }

    // уведомления когда админ создает мероприятие
    private fun sendNotification(message: String) {
        // Отправьте уведомление с помощью Firebase Cloud Messaging (FCM)
        val remoteMessage = RemoteMessage.Builder("SENDER_ID@gcm.googleapis.com")
            .setMessageId(java.lang.Long.toString(System.currentTimeMillis()))
            .addData("message", message)
            .build()
        MyFirebaseMessagingService().onMessageReceived(remoteMessage)
    }

}
