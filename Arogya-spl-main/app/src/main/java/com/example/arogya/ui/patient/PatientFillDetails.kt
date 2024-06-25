package com.example.arogya.ui.patient

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.arogya.R
import com.example.arogya.model.Patient
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson

class PatientFillDetails : AppCompatActivity() {


    private lateinit var dbRef : DatabaseReference

    private lateinit var patient: Patient
    private lateinit var submit : Button

    private lateinit var name : EditText
    private lateinit var healthIssue : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_fill_details)

        Toast.makeText(
            this,
            "Sign up process will complete after filling the details above",
            Toast.LENGTH_SHORT
        ).show()

        patient = Patient()
        dbRef = FirebaseDatabase.getInstance().getReference("Patient")

        Glide.with(this)
            .load(R.drawable.nakka_aathu)
            .into(findViewById(R.id.iv_logo))

        name = findViewById(R.id.et_name)
        healthIssue = findViewById(R.id.et_health_issue)
        submit = findViewById(R.id.btn_submit)

        patient.userName = intent.getStringExtra("userName").toString()
        patient.email = intent.getStringExtra("email").toString()
        patient.password = intent.getStringExtra("password").toString()

        submit.setOnClickListener {
            if(
                name.text.toString().isEmpty()||
                healthIssue.text.toString().isEmpty()
                    ){
                Toast.makeText(this, "Enter all fields", Toast.LENGTH_SHORT).show()
            }else{
                patient.name = name.text.toString()
                patient.healthIssue = healthIssue.text.toString()

                val newKey = dbRef.push().key
                if (newKey != null) {
                    dbRef.child(newKey).setValue(patient)

                    val intent = Intent(this, PatientHomeScreen::class.java)
                    intent.putExtra("userName", patient.userName)
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
        editor.putString("userName",patient.userName)
        editor.putString("logStatus","loggedOut")
        val patientJson = Gson().toJson(patient)
        editor.putString("patientDetails",patientJson)
        editor.apply()
    }
}