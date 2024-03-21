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
    suspend fun upsert(text: String): Long

    @Query("SELECT * FROM Event")
    fun getAllEvents(): LiveData<List<Event>>

    @Delete
    suspend fun deleteEvent(event: Event)

}