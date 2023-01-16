package com.example.treaasuresofwords.Model

class User(
    var name : String,var surname : String,
    var email : String,var number : String,
    var isSelectedLanguage : Boolean,pageLanguage : String,
    var languages : ArrayList<HashMap<String,Any>>
)
{
}