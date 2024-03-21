package com.example.repository

import com.example.db.EventDatabase
import com.example.model.Event

class EventRepository(
    val db: EventDatabase
)  {

    suspend fun upsert (event: Event) = db.getEventsDao().upsert(event)

    fun getSavedNews() = db.getEventsDao().getAllEvents()

    suspend fun deleteArticle(event: Event) = db.getEventsDao().deleteEvent(event)

}