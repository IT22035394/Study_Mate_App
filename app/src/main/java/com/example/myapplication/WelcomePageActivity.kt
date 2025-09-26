package com.example.myapplication // ← match your actual package

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button

class WelcomePageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Link to your XML layout (the welcome screen you posted)
        setContentView(R.layout.welcome_page)   // or R.layout.activity_welcome if that's your file name

        val btnLetsGo: Button = findViewById(R.id.btnLetsGo)

        btnLetsGo.setOnClickListener {
            val intent = Intent(this, StudentTeacherSelectionActivity::class.java)
            startActivity(intent)
            // finish()   // optional if you don’t want to return to this screen
        }
    }
}
