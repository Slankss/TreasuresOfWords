package com.example.treaasuresofwords.Model

class Question(
    var translate : String,
    var translated : String,
    var state : Boolean?,
    var index : Int,
    var level : Int

    /*
    var answer1 : String,
    var answer2 : String,
    var answer3 : String,
    var answer4 : String,
    var correctAnswer : String

     */

) {

    constructor(translate : String,translated: String,state: Boolean?,index : Int,level : Int,answer1 : String,answer2 : String,answer3 : String,answer4 : String)
            : this(translate,translated,state,index,level)



}