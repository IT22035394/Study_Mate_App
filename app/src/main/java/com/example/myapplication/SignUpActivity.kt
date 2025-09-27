package com.example.myapplication

import android.content.Intent
import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_page)

        dbHelper = DBHelper(this)

        val firstName = findViewById<EditText>(R.id.firstName)
        val lastName  = findViewById<EditText>(R.id.lastName)
        val email     = findViewById<EditText>(R.id.email)
        val phone     = findViewById<EditText>(R.id.phone)
        val password  = findViewById<EditText>(R.id.password)
        val confirm   = findViewById<EditText>(R.id.confirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        btnSignUp.setOnClickListener {

            val fName = firstName.text.toString().trim()
            val lName = lastName.text.toString().trim()
            val eMail = email.text.toString().trim()
            val pNum  = phone.text.toString().trim()
            val pass  = password.text.toString().trim()
            val cPass = confirm.text.toString().trim()

            // Basic validation
            if (fName.isEmpty() || lName.isEmpty() || eMail.isEmpty()
                || pNum.isEmpty() || pass.isEmpty() || cPass.isEmpty()
            ) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass != cPass) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Insert into database
            val inserted = dbHelper.insertUser(fName, lName, eMail, pNum, pass)

            if (inserted) {
                Toast.makeText(this, "Sign-Up successful. Please log in.", Toast.LENGTH_SHORT).show()

                // Navigate to LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                // optional: clear the sign-up screen from back stack
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } else {
                Toast.makeText(this, "Error saving user. Try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
