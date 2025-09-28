package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class UploadVideoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var selectedVideoUri: Uri? = null

    private lateinit var etVideoName: EditText
    private lateinit var etVideoDesc: EditText
    private lateinit var btnChoose: Button
    private lateinit var btnUpload: Button
    private lateinit var btnDiscard: Button

    companion object {
        private const val PICK_VIDEO_REQUEST = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload2)

        dbHelper = DBHelper(this)

        // Initialize views
        etVideoName = findViewById(R.id.etVideoName)
        etVideoDesc = findViewById(R.id.etVideoDesc)
        btnChoose = findViewById(R.id.btnChooseVideo)
        btnUpload = findViewById(R.id.btnUploadVideo)
        btnDiscard = findViewById(R.id.btnDiscard)

        // Back arrow → TeacherProfileActivity
        findViewById<ImageView>(R.id.ivProfile).setOnClickListener {
            finish()
        }

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    startActivity(Intent(this, TeacherProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Choose video
        btnChoose.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "video/*"
            }
            startActivityForResult(intent, PICK_VIDEO_REQUEST)
        }

        // Upload video
        btnUpload.setOnClickListener {
            val name = etVideoName.text.toString().trim()
            val desc = etVideoDesc.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || selectedVideoUri == null) {
                Toast.makeText(this, "Please fill all fields and select a video", Toast.LENGTH_SHORT).show()
            } else {
                val success = dbHelper.insertVideo(name, desc, selectedVideoUri.toString())
                if (success) {
                    Snackbar.make(btnUpload, "Video uploaded successfully!", Snackbar.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to upload video", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Discard video
        btnDiscard.setOnClickListener {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedVideoUri = data?.data
            selectedVideoUri?.let { uri ->
                // take persistable permission so we can use this URI later
                try {
                    contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Toast.makeText(this, "Video selected!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
