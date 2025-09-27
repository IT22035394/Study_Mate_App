package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class TeacherProfileActivity : AppCompatActivity() {

    private lateinit var db: DBHelper
    private lateinit var container: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teacher_profile)

        db = DBHelper(this)
        container = findViewById(R.id.videos_container)

        // Upload button → UploadVideoActivity
        findViewById<Button>(R.id.upload_video_button).setOnClickListener {
            startActivityForResult(Intent(this, UploadVideoActivity::class.java), 200)
        }

        // Bottom Navigation
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomeActivity::class.java))
                    true
                }
                R.id.navigation_profile -> true // already here
                else -> false
            }
        }

        loadVideos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if ((requestCode == 200 || requestCode == 201) && resultCode == RESULT_OK) {
            loadVideos() // refresh after upload or edit
        }
    }

    // --- Updated loadVideos with Edit + Delete ---
    private fun loadVideos() {
        container.removeAllViews()
        val videos = db.getAllVideos()
        findViewById<TextView>(R.id.videos_count).text = "${videos.size} Videos"

        for (video in videos) {
            val itemView = layoutInflater.inflate(R.layout.item_video_row, container, false)

            val videoView = itemView.findViewById<VideoView>(R.id.videoThumb)
            val title = itemView.findViewById<TextView>(R.id.videoTitle)
            val desc = itemView.findViewById<TextView>(R.id.videoDescription)
            val btnEdit = itemView.findViewById<ImageButton>(R.id.btnEditVideo)
            val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDeleteVideo)

            title.text = video["video_name"]
            desc.text = video["video_description"]

            videoView.setVideoURI(Uri.parse(video["video_uri"]))
            videoView.seekTo(100) // show preview frame

            // --- Edit button ---
            btnEdit.setOnClickListener {
                val intent = Intent(this, EditVideoActivity::class.java).apply {
                    putExtra("video_id", video["video_id"])
                    putExtra("video_name", video["video_name"])
                    putExtra("video_desc", video["video_description"])
                    putExtra("video_uri", video["video_uri"])
                }
                startActivityForResult(intent, 201)
            }

            // --- Delete button ---
            btnDelete.setOnClickListener {
                val deleted = db.deleteVideo(video["video_id"]!!.toInt())
                if (deleted) {
                    Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show()
                    loadVideos()
                }
            }

            container.addView(itemView)
        }
    }
}
