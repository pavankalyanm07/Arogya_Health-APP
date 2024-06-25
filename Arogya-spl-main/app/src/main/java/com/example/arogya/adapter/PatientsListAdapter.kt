package com.example.arogya.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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

class PatientsListAdapter (var context : Context,
                           private val patientsList: ArrayList<Patient>,
                           private val docUserName : String,
                           private var appointment: Appointment) :
    RecyclerView.Adapter<PatientsListAdapter.PatientViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val itemView = LayoutInflater.from(parent.context).
        inflate(R.layout.patient_list_item,parent,false)
        return PatientViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return patientsList.size
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val currentItem = patientsList[position]
        holder.name.text = currentItem.name
        holder.healthIssue.text = currentItem.healthIssue
        holder.timeSlot.text = currentItem.mySlot


        holder.cancel.setOnClickListener {


            val patientJason = Gson().toJson(currentItem)

            val updatedPatients = appointment.patients.filter { it != patientJason }
            appointment = appointment.copy(patients = updatedPatients)

            val updatedSlots = appointment.slots.filter { it != currentItem.mySlot }
            appointment = appointment.copy(slots = updatedSlots)

            appointment.patientsCount = appointment.patientsCount - 1

            removePatientFromAppointments(appointment)
            currentItem.zmyDoctor = Doctor()
            currentItem.mySlot = ""
            removeDoctorFromPatient(currentItem)

            patientsList.removeAt(position)
            notifyItemRemoved(position)

        }
    }

    private fun removePatientFromAppointments(appointment: Appointment) {
        val appointmentsDBReference = FirebaseDatabase.getInstance().getReference("Appointments")
        val query = appointmentsDBReference.orderByChild("userName").equalTo(docUserName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val node = snapshot.children.first()
                val key = node.key
                appointmentsDBReference.child(key!!).setValue(appointment)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun removeDoctorFromPatient(patient: Patient) {
        val patientDBReference = FirebaseDatabase.getInstance().getReference("Patient")
        val query = patientDBReference.orderByChild("userName").equalTo(patient.userName)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val node = snapshot.children.first()
                val key = node.key
                patientDBReference.child(key!!).setValue(patient)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    class PatientViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

        val name : TextView = itemView.findViewById(R.id.tv_patient_name)
        val healthIssue : TextView = itemView.findViewById(R.id.tv_health_issue)
        val timeSlot : TextView = itemView.findViewById(R.id.tv_time_slot)
        val cancel : Button = itemView.findViewById(R.id.btn_cancel)



    }
}

