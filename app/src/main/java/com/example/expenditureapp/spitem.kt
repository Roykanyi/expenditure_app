package com.example.expenditureapp

data class spitem(
    val title:String,
    val text:String,
    val type:SPType,
    val media:Int?=null
)
enum class SPType{
    Text,
    Video,
    Image,

}