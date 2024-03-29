package com.example.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.Event


@Database(
    entities =[Event::class],
    version = 1
)
abstract class EventDatabase: RoomDatabase() {

    abstract fun getEventsDao(): EventDao

    companion object {
        private var instance: EventDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDatabase(context).also { instance = it}
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                EventDatabase::class.java,
                "Events2.db"
            ).build()
    }
}