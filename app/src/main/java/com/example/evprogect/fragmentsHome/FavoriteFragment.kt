package com.example.evprogect.fragmentsHome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.example.evprogect.EvActivity
import com.example.evprogect.R
import com.example.evprogect.databinding.FragmentFavoriteBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FavoriteFragment : Fragment() {
    private lateinit var binding: FragmentFavoriteBinding
    private val database = Firebase.database

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventsRef = database.getReference("events")

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

        eventsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (lessonData in lessons.values) {
                    val eventId = lessonData.first
                    val isSaved = dataSnapshot.child(eventId).child("isSaved").getValue(Boolean::class.java) ?: false

                    val button = lessons.entries.find { it.value.first == eventId }?.key
                    button?.visibility = if (isSaved) View.VISIBLE else View.GONE
                }
            }



            override fun onCancelled(databaseError: DatabaseError) {
                // Обработка ошибок чтения из базы данных Firebase, если необходимо.
            }
        })

        eventsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)

                    // Проверяем, является ли статус события "новый"
                    if (event?.status == "новый") {
                        // Создаем новую карточку CardView
                        val newCardView = layoutInflater.inflate(R.layout.cardview_layout, null) as CardView

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
}
