package com.shubham.enamora.data.local.database

import android.content.Context
import androidx.room.Room

object EnamoraDatabaseProvider {

    private const val DATABASE_NAME =
        "enamora.db"

    @Volatile
    private var database:
            EnamoraDatabase? = null

    fun getDatabase(
        context: Context
    ): EnamoraDatabase {
        return database
            ?: synchronized(this) {
                database
                    ?: createDatabase(
                        context.applicationContext
                    ).also { createdDatabase ->
                        database = createdDatabase
                    }
            }
    }

    private fun createDatabase(
        context: Context
    ): EnamoraDatabase {
        return Room.databaseBuilder(
            context,
            EnamoraDatabase::class.java,
            DATABASE_NAME
        ).build()
    }
}