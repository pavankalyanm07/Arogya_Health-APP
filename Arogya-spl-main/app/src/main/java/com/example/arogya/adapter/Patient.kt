package com.example.arogya.model

data class Patient(
    var email : String,
    var healthIssue:String,
    var name : String,
    var password : String,
    var userName : String,
    var zmyDoctor: Doctor,
    var mySlot : String
){
    constructor() : this("","","","","",Doctor(),"")
}
