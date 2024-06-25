package com.example.arogya.ui.doctor

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.arogya.R
import com.example.arogya.model.Doctor
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class DoctorFillDetails : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference

    private lateinit var doctor : Doctor
    private lateinit var submit : Button

    private lateinit var name : EditText
    private lateinit var qualification : EditText
    private lateinit var experience : EditText
    private lateinit var field : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_fill_details)

        Toast.makeText(
            this,
            "Sin up process will finish after filling the details above",
            Toast.LENGTH_SHORT
        ).show()

        doctor = Doctor()
        dbRef = FirebaseDatabase.getInstance().getReference("Doctor")

        Glide.with(this)
            .load(R.drawable.arogya_gif)
            .into(findViewById(R.id.iv_logo))

        submit = findViewById(R.id.btn_submit)
        name = findViewById(R.id.et_name)
        qualification = findViewById(R.id.et_qualification)
        experience = findViewById(R.id.et_experience)
        field = findViewById(R.id.et_field)

        doctor.userName = intent.getStringExtra("userName").toString()
        doctor.email = intent.getStringExtra("email").toString()
        doctor.password = intent.getStringExtra("password").toString()

        submit.setOnClickListener {

            if (
                name.text.toString().isEmpty()||
                qualification.text.toString().isEmpty()||
                experience.text.toString().isEmpty()||
                field.text.toString().isEmpty()
            ){
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
            }
            else{
                doctor.name = name.text.toString()
                doctor.qualification = qualification.text.toString()
                doctor.experience = experience.text.toString()
                doctor.field = field.text.toString()

                val newKey = dbRef.push().key
                if (newKey != null) {
                    dbRef.child(newKey).setValue(doctor)
                    val intent = Intent(this, DoctorHomeScreen::class.java)
                    intent.putExtra("userName", doctor.userName)
                    saveDataInSharedPref()
                    startActivity(intent)
                }else{
                    Toast.makeText(this, "try again", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveDataInSharedPref() {
        val sharedPreferences = getSharedPreferences("patientFile", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userName",doctor.userName)
        editor.putString("logStatus","loggedIn")
        editor.apply()
    }
}