package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.app.Activity
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

        // navigate back to SignUp screen if they don't have an account
        txtSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val firstName = inputStudentID.text.toString().trim()
            val password  = inputPassword.text.toString().trim()

            if (firstName.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userExists = dbHelper.checkUser(firstName, password)

            if (userExists) {
                Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                // Redirect to HomeActivity
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra("FIRST_NAME", firstName)
                startActivity(intent)
                finish()
            } else {
                // Show a small inline message
                showErrorBelowForm()
            }
        }
    }

    private fun showErrorBelowForm() {
        // You can either add a TextView dynamically or show a Toast.
        // Here we’ll add a small TextView at the bottom for clarity.

        val errorText = findViewById<TextView?>(R.id.loginErrorMessage)
        if (errorText == null) {
            val rootLayout = findViewById<android.widget.RelativeLayout>(R.id.rootLayout)
            val tv = TextView(this).apply {
                id = R.id.loginErrorMessage
                text = "Login unsuccessful. Please sign up first."
                setTextColor(resources.getColor(android.R.color.holo_red_light))
                textSize = 14f
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
            rootLayout.addView(tv)
        }
    }
}
