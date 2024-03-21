package com.example.repository

import com.example.db.EventDatabase
import com.example.model.Event

class EventRepository(
    val db: EventDatabase
)  {

    suspend fun upsert (event: Event) = db.getEventsDao().upsert(event)

    fun getSavedEvents(date: String?) = db.getEventsDao().getEventsByDate(date)

    suspend fun deleteEvent(event: Event) = db.getEventsDao().deleteEvent(event)

}