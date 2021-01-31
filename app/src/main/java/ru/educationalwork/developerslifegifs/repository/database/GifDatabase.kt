package ru.educationalwork.developerslifegifs.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.educationalwork.developerslifegifs.repository.model.DbGifItem

@Database(entities = [DbGifItem::class], version = 1)
abstract class GifDatabase : RoomDatabase() {
    abstract fun getGifDao() : GifDao
}