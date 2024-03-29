package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Time
import java.util.Date

@Entity
data class Event (
    @PrimaryKey(autoGenerate = true)
    var id :Int? = null,
    var eventDescription : String? = null,
    var eventDate: String? = null,
    var eventAlarm: String? = null
    )