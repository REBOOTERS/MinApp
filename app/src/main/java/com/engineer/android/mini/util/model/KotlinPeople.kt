package com.engineer.android.mini.util.model

data class KotlinPeople(val name: String? = "", val address: String? = "")  {
    constructor() : this("","")
}