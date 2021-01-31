package ru.educationalwork.developerslifegifs.repository.database

import androidx.lifecycle.LiveData
import ru.educationalwork.developerslifegifs.repository.model.DbGifItem

interface GifRepositoryInterface {

    fun addGifToDb(gif: DbGifItem)

    fun getAllGifs() : List<DbGifItem>
}