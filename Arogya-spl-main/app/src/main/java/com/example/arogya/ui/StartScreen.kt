package com.example.arogya.ui

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.bumptech.glide.Glide
import com.example.arogya.R
import com.example.arogya.model.Patient
import com.example.arogya.ui.doctor.DoctorHomeScreen
import com.example.arogya.ui.patient.PatientHomeScreen
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class StartScreen : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var patientUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Glide.with(this)
            .load(R.drawable.arogya_gif)
            .into(findViewById(R.id.logo_gif))

        Handler().postDelayed(Runnable {


            if (patientLogStatus()=="loggedIn"){
                val i = Intent(this, PatientHomeScreen::class.java)
                startActivity(i)
                getPatientDetails()
                finish()
            }else if (doctorLogStatus()=="loggedIn"){
                val i = Intent(this, DoctorHomeScreen::class.java)
                startActivity(i)
                finish()
            }else{
                val i = Intent(this, WhoAmI::class.java)
                startActivity(i)
                finish()
            }

        }, 3000)

    }

    private fun getPatientDetails() {
        val query = dbRef.orderByChild("userName").equalTo(patientUserName)
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val patient = snapshot.children.first().getValue(Patient::class.java)
                    putPatientInSharedPref(patient)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun putPatientInSharedPref(patient: Patient?) {

        val sharedPreferences = applicationContext.getSharedPreferences("patientFile", Context.MODE_PRIVATE)
        val patientJason = Gson().toJson(patient)
        val editor = sharedPreferences.edit()
        editor.putString("patientDetails",patientJason)
        editor.apply()
    }

    private fun doctorLogStatus(): String {
        val sharedPreferences = applicationContext.getSharedPreferences("doctorFile", Context.MODE_PRIVATE)
        val defaultValue = "loggedOut"
        val retrievedValue = sharedPreferences.getString("logStatus", defaultValue)
        return retrievedValue.toString()
    }

    private fun patientLogStatus(): String {
        val sharedPreferences = applicationContext.getSharedPreferences("patientFile", Context.MODE_PRIVATE)
        val defaultValue = "loggedOut"
        val retrievedValue = sharedPreferences.getString("logStatus", defaultValue)
        patientUserName = sharedPreferences.getString("userName","").toString()
        return retrievedValue.toString()
    }

}