package com.example.kotlinartbookfragment.RoomDb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.kotlinartbookfragment.Model.Art


@Database(entities = [Art::class], version = 1)
abstract class ArtDatabase : RoomDatabase() {
    abstract fun artDao(): ArtDao
}