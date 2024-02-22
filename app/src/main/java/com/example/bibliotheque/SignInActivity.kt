package com.example.bibliotheque

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bibliotheque.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        firebaseAuth = FirebaseAuth.getInstance()

        // Bouton connexion
        binding.btnConnection.setOnClickListener {
            val mail = binding.mail.text.toString()
            val pwd = binding.password.text.toString()

            // Dialog champ mail vide
            val mailBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            mailBuilder
                .setMessage("Veuillez entrer une adresse mail")
                .setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }

            val mailDialog: AlertDialog = mailBuilder.create()

            // Dialog champ mdp vide
            val pwdBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            pwdBuilder
                .setMessage("Veuillez entrer un mot de passe")
                .setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }

            val pwdDialog: AlertDialog = pwdBuilder.create()

            // Dialog identifiants incorrect
            val exBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            exBuilder
                .setMessage("Identifiants incorrects")
                .setPositiveButton("OK") { dialog, which ->
                    dialog.dismiss()
                }

            val exDialog: AlertDialog = exBuilder.create()

            if(mail.isEmpty()) mailDialog.show()
            else if(pwd.isEmpty()) pwdDialog.show()
            else if (mail.isNotEmpty() && pwd.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(mail, pwd).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        exDialog.show()
                    }
                }
            }
        }

        // Renvoyer vers la page principale si l'utilisateur est déja connecté
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }
}