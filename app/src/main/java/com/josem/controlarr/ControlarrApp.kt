package com.josem.controlarr

import android.app.Application
import androidx.room.Room
import com.josem.controlarr.data.AppDatabase

class ControlarrApp : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(this, AppDatabase::class.java, "controlarr.db").build()
    }
}
