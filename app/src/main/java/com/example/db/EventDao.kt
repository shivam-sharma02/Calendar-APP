package com.example.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.model.Event

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(event: Event): Long

    @Query("SELECT * FROM Event")
    fun getAllEvents(): LiveData<List<Event>>

    @Delete
    suspend fun deleteEvent(event: Event)

    @Query("SELECT * FROM Event WHERE eventDate = :date")
    fun getEventsByDate(date: String?): LiveData<List<Event>>

}