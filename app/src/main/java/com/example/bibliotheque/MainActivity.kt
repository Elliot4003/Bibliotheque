package com.example.bibliotheque

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bibliotheque.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var firebaseStorage: FirebaseStorage
    private var mList = mutableListOf<String>()
    private lateinit var adapter : ImagesAdapter

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        initVars()
        getImages()

        // Bouton déconnexion
        binding.btnDeconnection.setOnClickListener {
            firebaseAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initVars() {
        firebaseAuth = FirebaseAuth.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = ImagesAdapter(mList)
        binding.recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getImages() {
        binding.progressBar.visibility = View.VISIBLE
        val listRef = firebaseStorage.getReference("books")
        listRef.listAll()
            .addOnSuccessListener { result ->
                for(item in result.items){
                    item.downloadUrl
                        .addOnSuccessListener { uri ->
                            val imageUrl = uri.toString()
                            Log.d("URLBook", imageUrl)
                            mList.add(imageUrl)
                            adapter.notifyDataSetChanged()
                        }
                }
                binding.progressBar.visibility = View.GONE
            }
            .addOnFailureListener{
                val exBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
                exBuilder
                    .setMessage("Une erreur est survenue")
                    .setPositiveButton("OK") { dialog, which ->
                        dialog.dismiss()
                    }

                val exDialog: AlertDialog = exBuilder.create()
                exDialog.show()

                binding.progressBar.visibility = View.GONE
            }
    }
}
