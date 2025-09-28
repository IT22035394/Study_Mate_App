package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        dbHelper = DBHelper(this)

        val inputStudentID = findViewById<EditText>(R.id.inputStudentID)
        val inputPassword  = findViewById<EditText>(R.id.inputPassword)
        val btnLogin       = findViewById<Button>(R.id.btnLogin)
        val txtSignUp      = findViewById<TextView>(R.id.txtSignUp)

        // Navigate to SignUp screen if user clicks
        txtSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        // Login button click
        btnLogin.setOnClickListener {
            val studentId = inputStudentID.text.toString().trim()
            val password  = inputPassword.text.toString().trim()

            if (studentId.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // ✅ Check DB for existing user
            val userExists = dbHelper.checkUser(studentId, password)

            if (userExists) {
                // ✅ Login successful → go to Home
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, HomeActivity::class.java).apply {
                    putExtra("FIRST_NAME", studentId)
                }
                startActivity(intent)
                finish()
            } else {
                // ✅ No such user in DB
                showErrorMessage("Login unsuccessful. Please sign up first.")
            }
        }
    }

    // Function to show error message below the form
    private fun showErrorMessage(message: String) {
        val rootLayout = findViewById<RelativeLayout>(R.id.rootLayout)
        var errorText = findViewById<TextView>(R.id.loginErrorMessage)

        if (errorText == null) {
            // Create error TextView if not already present
            errorText = TextView(this).apply {
                id = R.id.loginErrorMessage
                setTextColor(resources.getColor(android.R.color.holo_red_light))
                textSize = 14f
                text = message
                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.BELOW, R.id.txtSignUp)
                    addRule(RelativeLayout.CENTER_HORIZONTAL)
                    topMargin = 16
                }
                layoutParams = params
            }
            rootLayout.addView(errorText)
        } else {
            // If already present, just update text
            errorText.text = message
        }
    }
}
