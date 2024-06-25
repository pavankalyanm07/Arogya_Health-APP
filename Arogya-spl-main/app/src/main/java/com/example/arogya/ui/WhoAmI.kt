package com.example.arogya.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.arogya.R
import com.example.arogya.ui.doctor.DoctorLogin
import com.example.arogya.ui.patient.PatientLogin


class WhoAmI : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_who_am_i)

        findViewById<Button>(R.id.button_doctor).setOnClickListener {
            Intent(this, DoctorLogin::class.java).also {
                startActivity(it)
                finish()
            }
        }

        findViewById<Button>(R.id.button_patient).setOnClickListener {
            Intent(this, PatientLogin::class.java).also {
                startActivity(it)
                finish()
            }
        }

        findViewById<Button>(R.id.crash_button).setOnClickListener {
            throw RuntimeException("Test Crash")
        }



        /*val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var screenWidth = displayMetrics.widthPixels / displayMetrics.density
        screenWidth /= 2
        findViewById<Button>(R.id.button_doctor).animate().translationX(-200F).duration = 500
        findViewById<Button>(R.id.button_patient).animate().translationX(200F).duration = 500*/

    }
}