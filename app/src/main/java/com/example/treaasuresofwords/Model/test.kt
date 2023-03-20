package com.example.treaasuresofwords.Model

import android.util.Log

fun main(){

    var hashmap = HashMap<String,Any>()
    hashmap.put("name","okan")

    hashmap.put("name","mahmut")

    hashmap.put("name","ayşe")

    var array = arrayListOf<HashMap<String,Any>>()
    array.add(hashmap)

    array.get(0)["name"]
    array.get(0).put("name","doğu ibnesi")

   println(array.get(0)["name"].toString())

}