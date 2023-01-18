package com.example.treaasuresofwords.Model

class User(
    var username : String,
    var email : String,var number : String,
    var selectedLanguageState : Boolean,var pageLanguage : String,
    var languages : ArrayList<HashMap<String,Any>>
)
{
}