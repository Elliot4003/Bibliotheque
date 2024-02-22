package com.example.bibliotheque

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import com.example.bibliotheque.databinding.ActivityBookInfoBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception

class BookInfo : AppCompatActivity() {

     private lateinit var binding : ActivityBookInfoBinding
     private lateinit var firebaseFirestore: FirebaseFirestore

     @SuppressLint("SourceLockedOrientationActivity", "SetTextI18n")
     override fun onCreate(savedInstanceState: Bundle?) {
          super.onCreate(savedInstanceState)
          binding = ActivityBookInfoBinding.inflate(layoutInflater)
          setContentView(binding.root)
          requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

          firebaseFirestore = FirebaseFirestore.getInstance()

          val extras = intent.extras
          if (extras != null){
               val title = extras.getString("title").toString()
               val author = extras.getString("author").toString()
               val url = extras.getString("url").toString()

               Picasso.get()
                    .load(url)
                    .into(binding.image, object : Callback {
                         override fun onSuccess() {
                              binding.title.text = title
                              binding.author.text = author
                         }

                         override fun onError(e: Exception?) {
                              Log.e("Erreur", "Erreur lors du chargement des données")
                         }

                    })

               val listRef = firebaseFirestore.collection("books").whereEqualTo("book_img", url)

               listRef.get()
                    .addOnSuccessListener { documents ->
                         for(document in documents){
                              val isRead = document.getBoolean("isRead")
                              val date = document.getString("book_date")
                              val description = document.getString("book_description")

                              binding.checkbox.isChecked = isRead == true
                              binding.date.text = "Date de parution : $date"
                              binding.description.text = description
                         }
                    }

               binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked){
                         val data = hashMapOf("isRead" to true)
                         listRef.get()
                              .addOnSuccessListener { documents ->
                                   for (document in documents) {
                                        val idBook = document.id
                                        val book = firebaseFirestore.collection("books").document(idBook)
                                        book.update(data as Map<String, Any>)
                                   }
                              }

                    } else {
                         val data = hashMapOf("isRead" to false)
                         listRef.get()
                              .addOnSuccessListener { documents ->
                                   for (document in documents) {
                                        val idBook = document.id
                                        val book = firebaseFirestore.collection("books").document(idBook)
                                        book.update(data as Map<String, Any>)
                                   }
                              }
                    }
               }
          }

          binding.btnRetour.setOnClickListener {
               val intent = Intent(this, MainActivity::class.java)
               startActivity(intent)
          }

     }

}