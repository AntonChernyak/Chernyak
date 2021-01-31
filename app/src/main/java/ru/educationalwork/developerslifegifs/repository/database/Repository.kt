package ru.educationalwork.developerslifegifs.repository.database

import androidx.lifecycle.LiveData
import ru.educationalwork.developerslifegifs.repository.model.DbGifItem

class Repository(private val dao: GifDao) : GifRepositoryInterface {

    override fun addGifToDb(gif: DbGifItem) {
        dao.addGif(gif)
    }

    override fun getAllGifs(): LiveData<List<DbGifItem>>? {
        return dao.getAllGifs()
    }

}