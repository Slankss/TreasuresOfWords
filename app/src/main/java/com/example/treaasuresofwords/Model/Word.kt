package com.example.treaasuresofwords.Model

import java.sql.Timestamp

class Word
{

    var word = ""
    var translate = ""
    var repeatTime = 0
    var date = ""
    var quizTime = ""
    var index = 0



    constructor(word: String,translate: String,repeatTime : Int,date  :String,quizTime :String){
        this.word = word
        this.translate = translate
        this.repeatTime = repeatTime
        this.date = date
        this.quizTime = quizTime
    }

    constructor(word: String,translate: String,repeatTime : Int,date  :String,quizTime :String,index : Int){
        this.word = word
        this.translate = translate
        this.repeatTime = repeatTime
        this.date = date
        this.quizTime = quizTime
        this.index = index
    }


}