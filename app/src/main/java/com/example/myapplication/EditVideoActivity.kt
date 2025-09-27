package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditVideoActivity : AppCompatActivity() {

    private lateinit var dbHelper: DBHelper
    private var selectedVideoUri: Uri? = null
    private var videoId: Int = -1

    private lateinit var etVideoName: EditText
    private lateinit var etVideoDesc: EditText
    private lateinit var btnChoose: Button
    private lateinit var btnSave: Button
    private lateinit var btnDiscard: Button

    companion object {
        private const val PICK_VIDEO_REQUEST = 1002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)

        dbHelper = DBHelper(this)

        // Bind views
        etVideoName = findViewById(R.id.etVideoName)
        etVideoDesc = findViewById(R.id.etVideoDesc)
        btnChoose = findViewById(R.id.btnChooseVideo)
        btnSave = findViewById(R.id.btnUploadVideo)
        btnDiscard = findViewById(R.id.btnDiscardVideo)

        // Get extras from TeacherProfileActivity
        videoId = intent.getStringExtra("video_id")?.toInt() ?: -1
        val videoName = intent.getStringExtra("video_name") ?: ""
        val videoDesc = intent.getStringExtra("video_desc") ?: ""
        val videoUri = intent.getStringExtra("video_uri")

        etVideoName.setText(videoName)
        etVideoDesc.setText(videoDesc)
        if (videoUri != null) selectedVideoUri = Uri.parse(videoUri)

        // --- Choose another video
        btnChoose.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "video/*" }
            startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST)
        }

        // --- Save Changes
        btnSave.setOnClickListener {
            val name = etVideoName.text.toString().trim()
            val desc = etVideoDesc.text.toString().trim()

            if (name.isEmpty() || desc.isEmpty() || selectedVideoUri == null) {
                Toast.makeText(this, "Fill all fields and select a video", Toast.LENGTH_SHORT).show()
            } else {
                val success = dbHelper.updateVideo(videoId, name, desc, selectedVideoUri.toString())
                if (success) {
                    Toast.makeText(this, "Video updated successfully!", Toast.LENGTH_SHORT).show()
                    setResult(Activity.RESULT_OK)
                    finish() // back to TeacherProfile
                } else {
                    Toast.makeText(this, "Failed to update video", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // --- Discard Changes
        btnDiscard.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish() // back to TeacherProfile without changes
        }

        // --- Back arrow
        findViewById<ImageView>(R.id.ivProfile).setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        // --- Bottom Nav
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
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
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedVideoUri = data?.data
            if (selectedVideoUri != null) {
                Toast.makeText(this, "Video selected: ${selectedVideoUri.toString()}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
