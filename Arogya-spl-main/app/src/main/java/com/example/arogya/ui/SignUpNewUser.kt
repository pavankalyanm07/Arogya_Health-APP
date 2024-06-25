package com.example.arogya.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.arogya.R
import com.example.arogya.ui.doctor.DoctorFillDetails
import com.example.arogya.ui.patient.PatientFillDetails
import com.google.firebase.database.*

class SignUpNewUser : AppCompatActivity() {

    private lateinit var userType : String
    private lateinit var dbRef : DatabaseReference

    private lateinit var userName : EditText
    private lateinit var email : EditText
    private lateinit var password : EditText
    private lateinit var signUp : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up_new_user)

        userName = findViewById(R.id.et_user_name)
        email = findViewById(R.id.et_email)
        password = findViewById(R.id.et_password_sign_up)
        signUp = findViewById(R.id.btn_sign_up)


        Glide.with(this)
            .load(R.drawable.arogya_gif)
            .into(findViewById(R.id.iv_logo))

        userType = intent.getStringExtra("userType").toString()

        dbRef = FirebaseDatabase.getInstance().getReference("$userType")
        //Toast.makeText(this, "$userType", Toast.LENGTH_SHORT).show()


        signUp.setOnClickListener {
            if (
                userName.text.toString().isEmpty()||
                email.text.toString().isEmpty()||
                password.text.toString().isEmpty()
                    ){
                Toast.makeText(this, "Fill all the fields", Toast.LENGTH_SHORT).show()
            }
            else{
                val query = dbRef.orderByChild("userName").equalTo("${userName.text.toString()}")
                query.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // The value is present in the child nodes
                            Toast.makeText(applicationContext, "Try different UserName", Toast.LENGTH_LONG).show()

                        } else {
                            // The value is not present in the child nodes
                            /*var doctor = Doctor(email.text.toString(),"","","",
                                password.text.toString(),"",userName.text.toString())*/
                            if (userType == "Doctor"){
                                val intent = Intent(this@SignUpNewUser, DoctorFillDetails::class.java)
                                intent.putExtra("userName",userName.text.toString())
                                intent.putExtra("email",email.text.toString())
                                intent.putExtra("password",password.text.toString())
                                startActivity(intent)
                            }else{
                                val intent = Intent(this@SignUpNewUser, PatientFillDetails::class.java)
                                intent.putExtra("userName",userName.text.toString())
                                intent.putExtra("email",email.text.toString())
                                intent.putExtra("password",password.text.toString())
                                startActivity(intent)
                            }

                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle error
                    }
                })
            }
        }
    }
}