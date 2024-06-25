package com.example.arogya.ui.doctor

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.arogya.R
import com.example.arogya.adapter.PatientsListAdapter
import com.example.arogya.model.Appointment
import com.example.arogya.model.Patient
import com.example.arogya.ui.StartScreen
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.database.*
import com.google.gson.Gson

class DoctorHomeScreen : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference
    private lateinit var patientRecyclerView: RecyclerView
    private lateinit var patientsArrayList : ArrayList<Patient>
    private lateinit var toolBar : AppBarLayout

    private lateinit var account : Button
    private lateinit var logOut : Button

    private lateinit var docUserName : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor_home_screen)

        toolBar = findViewById(R.id.app_bar)

        account = findViewById(R.id.btn_account)
        logOut = findViewById(R.id.btn_log_out)

        patientRecyclerView = findViewById(R.id.rv_patients_list)
        patientRecyclerView.layoutManager = LinearLayoutManager(this)
        patientsArrayList = arrayListOf()

        logOut.setOnClickListener {
            val sharedPreferences = getSharedPreferences("doctorFile", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.remove("userName")
            editor.remove("logStatus")
            editor.apply()
            Intent(this, StartScreen::class.java).also {
                startActivity(it)
            }
            finish()
        }

        getDoctorUserName()
        getAppointmentsData()
    }

    private fun getDoctorUserName() {
        val sharedPref = this.getSharedPreferences("doctorFile", Context.MODE_PRIVATE)
        docUserName = sharedPref.getString("userName","").toString()
    }

    private fun getAppointmentsData() {
        dbRef = FirebaseDatabase.getInstance().getReference("Appointments")
        val query = dbRef.orderByChild("userName").equalTo(docUserName)
        query.addListenerForSingleValueEvent(object : ValueEventListener{

            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val appointment = snapshot.children.first().getValue(Appointment::class.java)
                    for (i in 0 until appointment?.patientsCount!!){
                        val patientJason = appointment.patients[i]
                        val patient = Gson().fromJson(patientJason,Patient::class.java)
                        patientsArrayList.add(patient)
                        /*Toast.makeText(
                            this@DoctorHomeScreen,
                            "data added to patientsArrayList",
                            Toast.LENGTH_SHORT
                        ).show()*/
                    }
                    patientRecyclerView.adapter = PatientsListAdapter(this@DoctorHomeScreen,
                        patientsArrayList,docUserName,appointment)
                }
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
}