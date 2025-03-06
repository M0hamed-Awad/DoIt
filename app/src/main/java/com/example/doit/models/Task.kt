package com.example.doit.models

import java.time.LocalDateTime

class Task(
    private val _id: Int,
    private var _title: String,
    private var _description: String,
    private var _deadline: LocalDateTime
) {

    val id: Int
        get() = _id

    var title: String
        get() = _title
        set(value){ if (value.isNotEmpty()) _title = value }

    var description: String
        get() = _description
        set(value) { _description = value }

    var deadline: LocalDateTime
        get() = _deadline
        set(value) {
            if ( value.isAfter(LocalDateTime.now()) )
                _deadline = value
            else
                throw Exception("Each new task should have a deadline later than the current time.")
        }
}