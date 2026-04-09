package com.josem.controlarr.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class Converters {
    @TypeConverter
    fun fromServerType(value: ServerType): String = value.name

    @TypeConverter
    fun toServerType(value: String): ServerType = ServerType.valueOf(value)
}

@Database(entities = [Server::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun serverDao(): ServerDao
}
