package ru.educationalwork.developerslifegifs.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.educationalwork.developerslifegifs.App
import ru.educationalwork.developerslifegifs.R
import ru.educationalwork.developerslifegifs.database.Db
import ru.educationalwork.developerslifegifs.model.DbGifItemModel
import ru.educationalwork.developerslifegifs.model.random_model.GifItemRandomResponse
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_LATEST = "latest"
        const val ACTION_HOT = "hot"
        const val ACTION_TOP = "top"
        const val SHARED_PREFERENCES_NAME = "gif-pref"
        const val COUNTER_KEY = "counter key"

        var counter : Int = 0
        var action: String = ""
        var page = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        counter = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(COUNTER_KEY, 0)
        if (counter == 0) loadRandomPostFromNet()
        else loadPostFromDb()
    }

    override fun onPause() {
        super.onPause()
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(
            COUNTER_KEY, counter).apply()
    }

    fun onClickPreviousButton(view: View) {
        counter++
        loadPostFromDb()
    }

    fun onClickNextButton(view: View) {
        imageButtonPrevious.isEnabled = true

        if (counter == 0) loadRandomPostFromNet()
        else {
            counter--
            loadPostFromDb()
        }

    }

    private fun loadRandomPostFromNet() {
        val randomGifCall = App.instance?.apiService?.getRandomPost()

        progressBar.visibility = View.VISIBLE
        randomGifCall?.enqueue(object : Callback<GifItemRandomResponse> {
            override fun onFailure(call: Call<GifItemRandomResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, getString(R.string.network_error), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<GifItemRandomResponse>, response: Response<GifItemRandomResponse>) {
                if (response.isSuccessful) {
                    setGifIntoView(response.body()?.gifURL)

                    Executors.newSingleThreadExecutor().execute {
                        //val gifDbItem = DbGifModel(response.body()!!.id, response.body()!!.gifURL)
                        val gifDbItem = DbGifItemModel(url =  response.body()!!.gifURL)
                        Db.getInstance(this@MainActivity)?.getGifDao()?.addRandomGif(gifDbItem)
                    }
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.error) + response.code(), Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    private fun loadSpecialPostFromNet(){
        val specialGifCall = App.instance?.apiService?.getSpecialPosts(action, page.toString())


    }

    private fun loadPostFromDb() {
       val gifList = Db.getInstance(this)?.getGifDao()?.getAllGifs()
        if (gifList?.size == counter) {
            imageButtonPrevious.isEnabled = false
            counter--
            Log.d("TAGGG", counter.toString())
            imageButtonPrevious.animate()
                .rotation(180f)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .setDuration(2000)
                .start()
        } else {
            val gif = gifList?.get(gifList.size - 1 - counter)
            setGifIntoView(gif?.url)
        }
    }

    fun setGifIntoView(url: String?) {
        Glide.with(imageViewGif.context)
            .asGif()
            .load(url)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?, isFirstResource: Boolean): Boolean {
                    Toast.makeText(this@MainActivity, getString(R.string.error) + e.toString(), Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    progressBar.visibility = View.GONE
                    return false
                }
            })
            .into(imageViewGif)
    }

    private fun bottomNavigationViewSettings() {

        val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_latest -> action = ACTION_LATEST
                    R.id.action_hot -> action = ACTION_HOT
                    R.id.action_top -> action = ACTION_TOP
                }
                true
            }

        val bnView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnView.setOnNavigationItemSelectedListener(navListener)
    }

    fun resetData(){
        counter = 0
        // и очистить БД
    }
}