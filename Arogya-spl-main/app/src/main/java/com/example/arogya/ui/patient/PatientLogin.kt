package com.example.arogya.ui.patient

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.bumptech.glide.Glide
import com.example.arogya.R
import com.example.arogya.model.Patient
import com.example.arogya.ui.SignUpNewUser
import com.google.firebase.database.*
import com.google.gson.Gson

class PatientLogin : AppCompatActivity() {

    private lateinit var dbRef : DatabaseReference

    private lateinit var userId : EditText
    private lateinit var password : EditText
    private lateinit var signIn : CardView
    private lateinit var signUp : TextView

    private lateinit var userName : String
    private lateinit var rememberMe : CheckBox



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_login)

        dbRef = FirebaseDatabase.getInstance().getReference("Patient")

        Glide.with(this)
            .load(R.drawable.arogya_gif)
            .into(findViewById(R.id.iv_logo))

        userId = findViewById(R.id.et_customer_id)
        password = findViewById(R.id.et_password)
        signIn = findViewById(R.id.cv_button)
        signUp = findViewById(R.id.tv_sign_up)
        rememberMe = findViewById(R.id.cb_remember_me)

        signUp.setOnClickListener {
            Intent(this, SignUpNewUser::class.java).also {
                it.putExtra("userType","Patient")
                startActivity(it)
            }
        }

        signIn.setOnClickListener {
            validatePatient(userId.text.toString(),password.text.toString())
        }
    }

    private fun validatePatient(userId: String, password: String) {
        val query = dbRef.orderByChild("userName").equalTo(userId)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val patient = snapshot.children.first().getValue(Patient::class.java)
                    if (patient != null) {
                        if (patient.password == password)
                        {
                            Intent(this@PatientLogin, PatientHomeScreen::class.java).also {
                                it.putExtra("userName", userId)
                                saveDataInSharedPref(patient)
                                startActivity(it)
                                finish()
                            }
                        }else{
                            Toast.makeText(
                                this@PatientLogin,
                                "Incorrect Password",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }else{
                        Toast.makeText(this@PatientLogin, "Try again", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val query = dbRef.orderByChild("email").equalTo(userId)
                    query.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            if (snapshot.exists()) {
                                val patient = snapshot.children.first().getValue(Patient::class.java)
                                if (patient != null) {
                                    if (patient.password == password)
                                    {
                                        Intent(this@PatientLogin, PatientHomeScreen::class.java).also {
                                            it.putExtra("userName", userId)
                                            saveDataInSharedPref(patient)
                                            startActivity(it)
                                            finish()
                                        }
                                    }else{
                                        Toast.makeText(
                                            this@PatientLogin,
                                            "Incorrect Password",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }else{
                                    Toast.makeText(this@PatientLogin, "Try again", Toast.LENGTH_SHORT).show()
                                }
                            }else{
                                Toast.makeText(this@PatientLogin, "SignUp First", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onCancelled(error: DatabaseError) {

                        }

                    })
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
            }
        })
    }

    private fun saveDataInSharedPref(patient : Patient) {
        val sharedPreferences = getSharedPreferences("patientFile", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("userName",patient.userName)
        if (rememberMe.isChecked){
            Toast.makeText(this, "RememberMe isChecked", Toast.LENGTH_SHORT).show()
            editor.putString("logStatus","loggedIn")
        }else{
            editor.putString("logStatus","loggedOut")
        }

        val patientJson = Gson().toJson(patient)
        editor.putString("patientDetails",patientJson)
        editor.apply()
    }
}