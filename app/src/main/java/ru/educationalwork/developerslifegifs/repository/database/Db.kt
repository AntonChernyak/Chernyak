package ru.educationalwork.developerslifegifs.repository.database

import android.content.Context
import androidx.room.Room
import ru.educationalwork.developerslifegifs.App.Companion.DATABASE_NAME

object Db {

    private var INSTANCE: GifDatabase? = null

    fun getInstance(context: Context): GifDatabase? {
        if (INSTANCE == null) {
            synchronized(GifDatabase::class) {

                INSTANCE = Room.databaseBuilder(
                    context,
                    GifDatabase::class.java,
                    DATABASE_NAME
                )
                    //.allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
        }
        return INSTANCE
    }

}