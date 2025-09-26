package com.example.myapplication  // must match the package at the top of the file

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class StudentTeacherSelectionActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_teacher_selection)
    }
}
