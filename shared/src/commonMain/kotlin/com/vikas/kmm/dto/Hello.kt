package com.vikas.kmm.dto

import kotlinx.serialization.Serializable


@Serializable
data class Data(
    val status: String,
    val data: List<Employee>,
){
    @Serializable
    data class Employee(
        val id: String,
        val employee_name: String,
        val employee_salary: String,
        val employee_age: String,
        val profile_image: String
    )
}
