package ru.educationalwork.developerslifegifs.repository.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.educationalwork.developerslifegifs.repository.model.DbGifItem

@Dao
interface GifDao {

    @Insert
    fun addGif(gifItem : DbGifItem)

    @Query("SELECT * FROM gifs_table ORDER BY id")
    fun getAllGifs(): LiveData<List<DbGifItem>>

}