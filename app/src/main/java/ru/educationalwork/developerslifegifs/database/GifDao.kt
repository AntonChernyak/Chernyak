package ru.educationalwork.developerslifegifs.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.educationalwork.developerslifegifs.model.DbGifItemModel

@Dao
interface GifDao {
    @Insert
    fun addRandomGif(gifItem : DbGifItemModel)

    @Query("SELECT * FROM gifs_table ORDER BY id")
    fun getAllGifs(): List<DbGifItemModel>


}