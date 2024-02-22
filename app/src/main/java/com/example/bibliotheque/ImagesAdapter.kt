package com.example.bibliotheque

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bibliotheque.databinding.ImagesBinding
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import java.lang.Exception
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.firestore.FirebaseFirestore

class ImagesAdapter(private var mList:List<String>) :
    RecyclerView.Adapter<ImagesAdapter.ImagesViewHolder>(){

    private lateinit var firebaseFirestore: FirebaseFirestore

    inner class ImagesViewHolder(var binding : ImagesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val binding = ImagesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        firebaseFirestore = FirebaseFirestore.getInstance()
        return ImagesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ImagesViewHolder, position: Int) {
        val url = mList[position]
        val listRef = firebaseFirestore.collection("books").whereEqualTo("book_img", url)

        with(holder.binding) {
            Picasso.get()
                .load(url)
                .into(image, object : Callback {
                    override fun onSuccess() {
                        listRef.get()
                            .addOnSuccessListener { documents ->
                                for (document in documents) {
                                    val titleStr = document.getString("book_title").toString()
                                    val authorStr = document.getString("book_author").toString()

                                    title.text = titleStr
                                    author.text = authorStr

                                    val isRead = document.getBoolean("isRead")
                                    if (isRead == false) {
                                        title.setTextColor(Color.parseColor("#FF2525"))
                                        author.setTextColor(Color.parseColor("#FF2525"))
                                    } else {
                                        title.setTextColor(Color.parseColor("#00AF08"))
                                        author.setTextColor(Color.parseColor("#00AF08"))
                                    }
                                }
                                image.setOnClickListener{
                                    val intent = Intent(root.context, BookInfo::class.java)
                                    intent.putExtra("title", title.text)
                                    intent.putExtra("author", author.text)
                                    intent.putExtra("url", url)
                                    root.context.startActivity(intent)
                                }
                            }
                            .addOnFailureListener {
                                Log.e("Erreur", "Erreur lors du chargement des données")
                            }
                    }

                    override fun onError(e: Exception?) {
                        Log.e("Erreur images", "Url: $url")
                    }
                })
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }

}