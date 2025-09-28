package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
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
        if ((requestCode == 200 || requestCode == 201) && resultCode == Activity.RESULT_OK) {
            loadVideos()
        }
    }

    /** Load all videos dynamically */
    private fun loadVideos() {
        container.removeAllViews()
        val videos = db.getAllVideos()

        // update video count text
        findViewById<TextView>(R.id.videos_count).text = "${videos.size} Videos"

        for (video in videos) {
            val itemView = layoutInflater.inflate(R.layout.item_video_row, container, false)

            val videoView = itemView.findViewById<VideoView>(R.id.videoThumb)
            val title = itemView.findViewById<TextView>(R.id.videoTitle)
            val desc = itemView.findViewById<TextView>(R.id.videoDescription)
            val btnEdit = itemView.findViewById<ImageButton>(R.id.btnEditVideo)
            val btnDelete = itemView.findViewById<ImageButton>(R.id.btnDeleteVideo)

            val videoName = video["video_name"] ?: "Untitled"
            val videoDesc = video["video_description"] ?: ""
            val videoUriString = video["video_uri"]

            title.text = videoName
            desc.text = videoDesc

            if (videoUriString != null) {
                try {
                    val uri = Uri.parse(videoUriString)
                    videoView.setVideoURI(uri)
                    videoView.seekTo(1) // just load first frame
                    // Click → open VideoPlayerActivity with ExoPlayer
                    itemView.setOnClickListener {
                        val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                            putExtra("video_uri", uri.toString())
                            putExtra("video_name", videoName)
                        }
                        startActivity(intent)
                    }
                } catch (e: Exception) {
                    Log.e("TeacherProfile", "Invalid video URI: $videoUriString", e)
                    Toast.makeText(this, "Invalid video for $videoName", Toast.LENGTH_SHORT).show()
                }
            }

            // --- Edit button ---
            btnEdit.setOnClickListener {
                val intent = Intent(this, EditVideoActivity::class.java).apply {
                    putExtra("video_id", video["video_id"]?.toInt() ?: -1)
                    putExtra("video_name", videoName)
                    putExtra("video_desc", videoDesc)
                    putExtra("video_uri", videoUriString)
                }
                startActivityForResult(intent, 201)
            }

            // --- Delete button ---
            btnDelete.setOnClickListener {
                val deleted = db.deleteVideo(video["video_id"]!!.toInt())
                if (deleted) {
                    Toast.makeText(this, "Video deleted", Toast.LENGTH_SHORT).show()
                    loadVideos()
                } else {
                    Toast.makeText(this, "Failed to delete video", Toast.LENGTH_SHORT).show()
                }
            }

            container.addView(itemView)
        }
    }
}
