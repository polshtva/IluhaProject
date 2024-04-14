package com.example.evprogect.fragmentsHome

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.*
import com.example.evprogect.R
import com.example.evprogect.User
import com.example.evprogect.UserViewModel

class SettingsFragment : Fragment() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var userNameEditText: EditText
    private lateinit var loginEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var btnSignIn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)
        val userId = userViewModel.userId

        // Получаем ссылки на элементы интерфейса
        var text = view.findViewById<TextView>(R.id.textHello)
        userNameEditText = view.findViewById(R.id.user_name_edit_text)
        loginEditText = view.findViewById(R.id.login_edit_text)
        passwordEditText = view.findViewById(R.id.password_edit_text)
        btnSignIn = view.findViewById(R.id.btnSingIn)

        // Получаем текущие данные пользователя из базы данных Firebase Realtime Database и отображаем их
        val database = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(User::class.java)
                if (user != null) {
                    // Устанавливаем полученные данные в соответствующие поля ввода
                    text.text = "Привет, ${user.name}"
                    userNameEditText.setText(user.name)
                    loginEditText.setText(user.login)
                    passwordEditText.setText(user.password)

                    // Получаем значение darkModeEnabled из базы данных и устанавливаем тему
                    val darkModeEnabled = user.darkModeEnabled
                    setThemeAccordingToDatabase(darkModeEnabled)
                } else {
                    Toast.makeText(requireContext(), "Данные пользователя не найдены", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(requireContext(), "Ошибка при получении данных пользователя: ${databaseError.message}", Toast.LENGTH_SHORT).show()
            }
        })

        // Получаем ссылку на переключатель темы
        val themeSwitch = view.findViewById<Switch>(R.id.theme_switch)
        themeSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Изменяем тему фрагмента в зависимости от состояния переключателя
            if (isChecked) {
                setThemeAccordingToDatabase(true)
                // Сохраняем состояние темы в базе данных Firebase
                saveThemeState(userId, true)
            } else {
                setThemeAccordingToDatabase(false)
                // Сохраняем состояние темы в базе данных Firebase
                saveThemeState(userId, false)
            }

            Toast.makeText(requireContext(), userId.toString(), Toast.LENGTH_LONG).show()
        }

        // Обработчик нажатия кнопки "Sign In"
        btnSignIn.setOnClickListener {
            // Получаем новые данные из полей EditText
            val newName = userNameEditText.text.toString()
            val newLogin = loginEditText.text.toString()
            val newPassword = passwordEditText.text.toString()

            // Обновляем данные пользователя в базе данных Firebase
            database.child("name").setValue(newName)
            database.child("login").setValue(newLogin)
            database.child("password").setValue(newPassword)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Данные успешно обновлены", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Ошибка при обновлении данных: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Метод для сохранения состояния темы в базе данных Firebase
    private fun saveThemeState(userId: String, isDarkModeEnabled: Boolean) {
        val context = context ?: return // Проверка на null для контекста
        val database = FirebaseDatabase.getInstance().reference.child("users").child(userId)
        database.child("darkModeEnabled").setValue(isDarkModeEnabled)
            .addOnSuccessListener {
                // Успешно сохранено
                Toast.makeText(context, "Состояние темы успешно сохранено", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                // Ошибка при сохранении состояния темы
                Toast.makeText(context, "Ошибка при сохранении состояния темы: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Метод для установки темы в соответствии с базой данных
    private fun setThemeAccordingToDatabase(darkModeEnabled: Boolean) {
        if (darkModeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}
