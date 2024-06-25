package com.example.arogya.ui.patient

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arogya.R
import com.example.arogya.adapter.DoctorListAdapter
import com.example.arogya.adapter.OnPatientClickActionListener
import com.example.arogya.model.Doctor
import com.example.arogya.model.Patient
import com.example.arogya.ui.StartScreen
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.*
import com.google.gson.Gson

class PatientHomeScreen : AppCompatActivity() , OnPatientClickActionListener{

    private lateinit var dbRef : DatabaseReference
    private lateinit var doctorRecyclerView: RecyclerView
    private lateinit var doctorArrayList : ArrayList<Doctor>
    private lateinit var toolBar : AppBarLayout

    private lateinit var account : Button
    private lateinit var logOut : Button

    private lateinit var goToNextScreen : CardView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_home_screen)

        toolBar = findViewById(R.id.app_bar)

        account = findViewById(R.id.btn_account)
        logOut = findViewById(R.id.btn_log_out)

        goToNextScreen = findViewById(R.id.cv_click_account_button)

        doctorRecyclerView = findViewById(R.id.rv_doctors_list)
        doctorRecyclerView.layoutManager = LinearLayoutManager(this)
        doctorArrayList = arrayListOf(/*Doctor("","6 years","Neurology",
            "Dr.Gochi","","MBBS","")*/)
        //doctorRecyclerView.adapter = DoctorListAdapter(doctorArrayList)
        //doctorRecyclerView.setHasFixedSize(true)

        //checkIfUserHasBookedAppointment()

        account.setOnClickListener {
            Intent(this,PatientAccount::class.java).also {
                startActivity(it)
            }
        }

        logOut.setOnClickListener {
            val sharedPreferences = this.getSharedPreferences("patientFile", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("userName")
            editor.remove("logStatus")
            editor.remove("patientDetails")
            editor.apply()
            Intent(this,StartScreen::class.java).also {
                startActivity(it)
            }
            finish()
        }

        getDoctorsData()
    }

    override fun onResume() {
        super.onResume()
        checkIfUserHasBookedAppointment()
    }
    private fun checkIfUserHasBookedAppointment() {
        val patient = getPatientDetails()
        if (patient.mySlot != ""){
            doctorRecyclerView.visibility = View.INVISIBLE
            goToNextScreen.visibility = View.VISIBLE
        }
    }

    private fun getPatientDetails(): Patient {
        val sharedPreferences = this.getSharedPreferences("patientFile", Context.MODE_PRIVATE)
        val patientJson = sharedPreferences.getString("patientDetails", "")
        val patient = Gson().fromJson(patientJson, Patient::class.java)
        return patient
    }

    private fun getDoctorsData() {
        //FirebaseApp.initializeApp(this);
        dbRef = FirebaseDatabase.getInstance().getReference("Doctor")
        dbRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (doctorSnapshot in snapshot.children){
                    val doctor = doctorSnapshot.getValue(Doctor::class.java)
                    doctorArrayList.add(doctor!!)
                }
                doctorRecyclerView.adapter = DoctorListAdapter(this@PatientHomeScreen,
                    doctorArrayList,object :OnPatientClickActionListener{
                    override fun onConfirmDoctorClicked() {
                        goToNextScreen.visibility = View.VISIBLE
                        /*Toast.makeText(
                            this@PatientHomeScreen,
                            "confirm button clicked MainActivity",
                            Toast.LENGTH_SHORT
                        ).show()*/
                        doctorRecyclerView.visibility = View.INVISIBLE
                    }

                })
                Log.d(TAG, "onDataChange: doc")
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.nav_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.nav_account -> Toast.makeText(this, "Account", Toast.LENGTH_SHORT).show()
            R.id.nav_logout -> Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onConfirmDoctorClicked() {

    }
}