package ru.educationalwork.developerslifegifs.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.educationalwork.developerslifegifs.model.DbGifItemModel

@Database(entities = [DbGifItemModel::class], version = 1)
abstract class GifDatabase : RoomDatabase() {
    abstract fun getGifDao() : GifDao
}