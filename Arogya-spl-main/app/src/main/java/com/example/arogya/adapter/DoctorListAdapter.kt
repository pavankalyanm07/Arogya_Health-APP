package com.example.arogya.adapter

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.arogya.R
import com.example.arogya.model.Appointment
import com.example.arogya.model.Doctor
import com.example.arogya.model.Patient
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson

class DoctorListAdapter(var context : Context, private val doctorsList : ArrayList<Doctor>,listener : OnPatientClickActionListener) : RecyclerView.Adapter<DoctorListAdapter.DoctorViewHolder>() {

    //var context = context
    private var appointmentsRef = FirebaseDatabase.getInstance().getReference("Appointments")

    val mListener : OnPatientClickActionListener =listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.doctor_list_view, parent,false)
        return DoctorViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return doctorsList.size

    }
    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val currentItem = doctorsList[position]
        holder.name.text = currentItem.name
        holder.qualification.text = currentItem.qualification
        holder.experience.text = currentItem.experience
        holder.field.text = currentItem.field

        holder.book.setOnClickListener {
            showAlertDialogue(position)
        }
    }

    private fun showAlertDialogue( position: Int) {
        val builder = AlertDialog.Builder(context)
        val view = View.inflate(context,R.layout.alert_layout_for_patient, null)


        val textView : TextView = view.findViewById(R.id.tv_doctor_booking_status)
        val slotEntry : EditText = view.findViewById(R.id.et_enter_slot)
        val confirmBooking : Button = view.findViewById(R.id.btn_confirm_booking)

        //val textForTextField = getTextForTextField(position)
        getTextForTextField(position,object : TextCallback{
            override fun onTextReceived(text: String) {
                if (text == ""){
                    textView.text = "Enter your desired time slot below"
                }
                else if (text=="full"){
                    textView.text = "All time slots for ${doctorsList[position].name} are booked"
                    slotEntry.visibility = View.GONE
                    confirmBooking.visibility = View.GONE
                }else{
                    textView.text = textView.text.toString() + text
                }
            }
        })

        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        confirmBooking.setOnClickListener {
            if (slotEntry.text.toString()!=""){
                bookTheSlot(position,slotEntry.text.toString())
            }
            dialog.dismiss()

            mListener.onConfirmDoctorClicked()
        }
    }

    private fun bookTheSlot(position: Int ,slotEntry: String) {
        val patient : Patient = getPatientDetails()
        patient.mySlot = slotEntry
        patient.zmyDoctor = doctorsList[position]
        enterDetailsInDataBase(position,slotEntry,patient)
    }

    private fun enterDetailsInDataBase(position: Int, slotEntry: String, patient: Patient) {

        val userName = doctorsList[position].userName
        val query = appointmentsRef.orderByChild("userName").equalTo(userName)
        //Toast.makeText(context, "inside enterDetailsInDataBase : userName $userName ", Toast.LENGTH_SHORT).show()
        Log.d("doctorListAdapter", "onDataChange: inside enterDetailsInDataBase")
        query.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    //Toast.makeText(context, "inside snapshot.exists : ", Toast.LENGTH_SHORT).show()
                    Log.d("doctorListAdapter", "onDataChange: inside snapshot.exists")
                    val appointment = snapshot.children.first().getValue(Appointment::class.java)
                    if (appointment != null) {
                        //Toast.makeText(context, "inside appointment != null", Toast.LENGTH_SHORT).show()
                        Log.d("doctorListAdapter", "onDataChange: inside appointment" +
                                " != null , patientsCount: ${appointment.patientsCount}")

                        val node = snapshot.children.first()
                        val key = node.key


                        val updatedPatientsList = appointment.patients.toMutableList()
                        updatedPatientsList.add(appointment.patientsCount,Gson().toJson(patient)) //= Gson().toJson(patient)
                        appointment.patients = updatedPatientsList.toTypedArray().toList()

                        val updatedSlotsList = appointment.slots.toMutableList()
                        updatedSlotsList.add(appointment.patientsCount,slotEntry) //[appointment.patientsCount] = slotEntry
                        appointment.slots = updatedSlotsList.toTypedArray().toList()

                        appointment.patientsCount = appointment.patientsCount + 1

                        /*Toast.makeText(
                            context,
                            "updating the appointment object",
                            Toast.LENGTH_SHORT
                        ).show()*/


                        appointmentsRef.child(key!!).setValue(appointment)


                        storePatientValueInSharedPref(patient)
                        updatePatientDetailsInFireBase(patient)
                    }
                }else{

                    val newKey = appointmentsRef.push().key

                    if (newKey != null) {


                        val patients = mutableListOf<String>()
                        patients.add(0, Gson().toJson(patient))

                        val slots = mutableListOf<String>()
                        slots.add(0,slotEntry)

                        val patientsCount = 1
                        val appointment = Appointment(userName,patients,slots,patientsCount)
                        appointmentsRef.child(newKey).setValue(appointment)

                        //Toast.makeText(context, "creating new appointment", Toast.LENGTH_SHORT).show()


                        storePatientValueInSharedPref(patient)
                        updatePatientDetailsInFireBase(patient)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }

    private fun updatePatientDetailsInFireBase(patient: Patient) {
        val patientDBReference = FirebaseDatabase.getInstance().getReference("Patient")
        val query = patientDBReference.orderByChild("userName").equalTo(patient.userName)
        query.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                val node = snapshot.children.first()
                val key = node.key
                patientDBReference.child(key!!).setValue(patient)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun storePatientValueInSharedPref(patient: Patient) {

        val sharedPreferences = context.getSharedPreferences("patientFile", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("patientDetails",Gson().toJson(patient))
        editor.apply()
    }

    private fun getTextForTextField(position: Int , callback: TextCallback): String {
        val userName = doctorsList[position].userName
        var textToReturn = "full"
        val query = appointmentsRef.orderByChild("userName").equalTo(userName)

        query.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()){
                    textToReturn = ""
                    //Toast.makeText(context, "inside onDataChack=ke", Toast.LENGTH_SHORT).show()
                    callback.onTextReceived(textToReturn)
                }
                else{
                    val appointment = snapshot.children.first().getValue(Appointment::class.java)
                    val patientsCount = appointment?.patientsCount
                    val slots = appointment?.slots
                    if (patientsCount == 5){
                        textToReturn = "full"
                        callback.onTextReceived(textToReturn)
                    }else{
                        textToReturn = ""
                        for (i in 0 until patientsCount!!){
                            textToReturn = slots?.get(i)!! + "\n"
                        }
                        callback.onTextReceived(textToReturn)
                    }

                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        return textToReturn
    }

    private fun getPatientDetails(): Patient {
        val sharedPreferences = context.getSharedPreferences("patientFile", Context.MODE_PRIVATE)
        val patientJson = sharedPreferences.getString("patientDetails", "")
        val patient = Gson().fromJson(patientJson, Patient::class.java)
        return patient
    }



    class DoctorViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.doctor_name)
        val qualification : TextView = itemView.findViewById(R.id.doctor_qualification)
        val experience : TextView = itemView.findViewById(R.id.doctor_experience)
        val field : TextView = itemView.findViewById(R.id.doctor_field)
        val book : Button = itemView.findViewById(R.id.btn_book_now)




    }


}


interface TextCallback {
    fun onTextReceived(text: String)
}

interface OnPatientClickActionListener {
    fun onConfirmDoctorClicked()
}