package com.example.arogya.model

data class Appointment(
    var userName: String,
    var patients:  List<String> = mutableListOf(),
    var slots : List<String> = mutableListOf(),
    var patientsCount : Int
) {
    constructor() : this("", mutableListOf(), mutableListOf(),0)
}
