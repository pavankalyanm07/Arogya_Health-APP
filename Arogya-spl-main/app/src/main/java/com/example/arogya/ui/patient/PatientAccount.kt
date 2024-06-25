 package com.example.arogya.ui.patient

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.arogya.R
import com.example.arogya.model.Appointment
import com.example.arogya.model.Doctor
import com.example.arogya.model.Patient
import com.google.firebase.database.*
import com.google.gson.Gson

 class PatientAccount : AppCompatActivity() {

     private lateinit var userNameTextView : TextView
     private lateinit var name : TextView
     private lateinit var qualification : TextView
     private lateinit var experience : TextView
     private lateinit var field : TextView
     private lateinit var slot : TextView
     private lateinit var book : Button
     private lateinit var doctor : Doctor

     private lateinit var appointmentDBRef : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_account)

        appointmentDBRef = FirebaseDatabase.getInstance().getReference("Appointments")

        name = findViewById(R.id.doctor_name)
        qualification = findViewById(R.id.doctor_qualification)
        experience = findViewById(R.id.doctor_experience)
        field = findViewById(R.id.doctor_field)
        //book = findViewById(R.id.btn_book_now)
        userNameTextView = findViewById(R.id.tv_hi_user)
        slot = findViewById(R.id.tv_booking_slot)

        val patient = getDetailsFromSharedPref()

        doctor = patient.zmyDoctor

        userNameTextView.text = "Hello "+ patient.name

        name.text = doctor.name
        qualification.text = doctor.qualification
        experience.text = doctor.experience
        field.text = doctor.field
        slot.text ="Booking Slot : "+ patient.mySlot


        //fillTheSlot()
    }

     private fun fillTheSlot() {
         val query = appointmentDBRef.orderByChild("userName").equalTo(doctor.userName)

         query.addListenerForSingleValueEvent(object :ValueEventListener{
             override fun onDataChange(snapshot: DataSnapshot) {
                 if (snapshot.exists()){
                     val appointment = snapshot.children.first().getValue(Appointment::class.java)
                     slot.text = "Booking Slot : ${appointment}"
                 }
             }

             override fun onCancelled(error: DatabaseError) {}

         })
     }

     private fun getDetailsFromSharedPref(): Patient {
         val sharedPreferences = getSharedPreferences("patientFile", Context.MODE_PRIVATE)
         val patientJson = sharedPreferences.getString("patientDetails", "")
         val patient = Gson().fromJson(patientJson, Patient::class.java)
         return patient
     }
 }