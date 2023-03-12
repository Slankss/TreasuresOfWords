package com.example.treaasuresofwords.Model

class Question{

    var translate = ""
    var translated = ""
    var state : Boolean? = null
    var index = 0
    var level = 0
    var answerList = arrayListOf<String>()



    constructor(translate : String,translated: String,state: Boolean?,index : Int,level: Int,
                answerList: ArrayList<String>){
        this.translate = translate
        this.translated = translated
        this.state = state
        this.index = index
        this.level = level
        this.answerList = answerList
    }

    constructor(translate : String,translated: String,state: Boolean?,index : Int,level: Int){
        this.translate = translate
        this.translated = translated
        this.state = state
        this.index = index
        this.level = level
    }


}