package com.example.evprogect.fragmentsHome

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.evprogect.EvActivity
import com.example.evprogect.R
import com.example.evprogect.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val database = Firebase.database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventsRef = database.getReference("events")

        val textViews = listOf(binding.idallEvents, binding.idSpeck, binding.idKon,
            binding.idF, binding.idVs, binding.idMclass, binding.idKvest)

        // Устанавливаем idallEvents с черным текстом по умолчанию
        binding.idallEvents.setTextColor(Color.BLACK)

        // Устанавливаем обработчики нажатий для всех TextView, кроме idallEvents
        textViews.filterNot { it == binding.idallEvents }.forEach { textView ->
            textView.setOnClickListener {
                // Сброс цвета всех TextView на белый
                textViews.forEach { it.setTextColor(Color.WHITE) }

                // Установка цвета текста текущего TextView на черный
                textView.setTextColor(Color.BLACK)

                // Вызов метода для фильтрации событий по типу
                filterByType(textView.text.toString())
            }
        }

        // Отдельный обработчик нажатия для idallEvents
        binding.idallEvents.setOnClickListener {
            // Сброс цвета всех TextView на белый
            textViews.forEach { it.setTextColor(Color.WHITE) }

            // Установка idallEvents с черным текстом
            binding.idallEvents.setTextColor(Color.BLACK)

            // Вызов метода для отображения всех событий
            showAllEvents()
        }

//        // Обработка нажатий
//        binding.idallEvents.setOnClickListener {
//            showAllEvents()
//        }

        val lessons = mapOf(
            binding.cardAddams to Pair("addams", R.drawable.addams),
            binding.cardJakone to Pair("jakone", R.drawable.j),
            binding.cardZem to Pair("zem", R.drawable.zem),
            binding.cardSibF to Pair("SibF", R.drawable.sibf),
            binding.cardEvOn to Pair("EvOn", R.drawable.onegin),
            binding.carddin to Pair("din", R.drawable.din),
            binding.cardmasterCl to Pair("mclass", R.drawable.masterclass),
            binding.cardSocrTob to Pair("SocrTob", R.drawable.sokrtb),
            binding.cardMum to Pair("mum", R.drawable.mum),
            binding.cardLOz to Pair("LOz", R.drawable.leboz)
        )

        lessons.forEach { (button, lessonData) ->
            button.setOnClickListener {
                val intent = Intent(requireContext(), EvActivity::class.java).apply {
                    putExtra("eventId", lessonData.first)
                    putExtra("urlImg", lessonData.second)
                }
                startActivity(intent)
            }
        }

        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return  // Проверяем, присоединен ли фрагмент

                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)

                    // Проверяем, является ли статус события "новый"
                    if (event?.status == "новый") {
                        // Создаем новую карточку CardView
                        val newCardView =
                            layoutInflater.inflate(R.layout.cardview_layout, null) as CardView

                        // Генерируем уникальный ID для карточки из ID события
                        val eventId = eventSnapshot.key
                        newCardView.id = eventId.hashCode() // Присваиваем ID карточке

                        // Устанавливаем тег с типом события для использования при фильтрации
                        newCardView.tag = event.type

                        // Заполняем данные в карточке из базы данных
                        newCardView.findViewById<TextView>(R.id.dataNew).text = event.date
                        newCardView.findViewById<TextView>(R.id.nameNew).text = event.name
                        newCardView.findViewById<TextView>(R.id.typeNew).text = event.type

                        // Добавляем карточку в LinearLayout
                        binding.events.addView(newCardView)

                        // Добавляем обработчик нажатия на новую карточку
                        newCardView.setOnClickListener {
                            // Создаем Intent для перехода на новую Activity
                            val intent = Intent(requireContext(), EvActivity::class.java).apply {
                                putExtra("eventId", eventSnapshot.key) // Передаем ID события
                                putExtra("urlImg", R.drawable.img_event) // Передаем URL изображения
                            }
                            startActivity(intent)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Обработка ошибок
            }
        })
    }

    private fun filterByType(type: String) {
        val cardContainer = binding.events
        for (i in 0 until cardContainer.childCount) {
            val card = cardContainer.getChildAt(i)
            when (card.id) {
                R.id.cardAddams -> {
                    if (type == "Спектакль") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.cardJakone -> {
                    if (type == "Концерт") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.cardZem -> {
                    if (type == "Концерт") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.cardSibF -> {
                    if (type == "Фестиваль") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.cardEvOn -> {
                    if (type == "Спектакль") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.carddin -> {
                    if (type == "Выставка") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.cardmasterCl -> {
                    if (type == "Мастер-класс") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.cardSocrTob -> {
                    if (type == "Квест") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.cardMum -> {
                    if (type == "Концерт") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                R.id.cardLOz -> {
                    if (type == "Спектакль") {
                        card.visibility = View.VISIBLE
                    } else {
                        card.visibility = View.GONE
                    }
                }
                else -> card.visibility = View.GONE
            }
        }
    }

    private fun showAllEvents() {
        val cardContainer = binding.events
        for (i in 0 until cardContainer.childCount) {
            val card = cardContainer.getChildAt(i)
            card.visibility = View.VISIBLE
        }
    }
}

data class Event(
    val date: String? = null,
    val name: String? = null,
    val type: String? = null,
    val status: String? = null
)
