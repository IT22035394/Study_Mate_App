package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val firstName = intent.getStringExtra("FIRST_NAME")
        val tvWelcome = findViewById<TextView>(R.id.tvWelcome)
        tvWelcome.text = "Welcome, $firstName!"

        // navigation bar connection
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)

            // optional: set currently selected item
            // bottomNav.selectedItemId = R.id.nav_home   // adjust to your menu id

            bottomNav.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.navigation_profile -> {     // <-- this ID must match your menu XML
                        val intent = Intent(this, TeacherProfileActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    R.id.navigation_profile -> {
                        // stay on home, maybe refresh
                        true
                    }
                    // handle other menu items if you have them
                    else -> false
                }
            }
        }

    }

