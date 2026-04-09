package com.josem.controlarr

import android.app.Application
import androidx.room.Room
import com.josem.controlarr.data.AppDatabase
import com.josem.controlarr.data.MIGRATION_1_2
import com.josem.controlarr.data.MIGRATION_2_3

class ControlarrApp : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "controlarr.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .fallbackToDestructiveMigration()
            .build()
    }
}
