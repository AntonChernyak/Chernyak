package ru.educationalwork.developerslifegifs.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import ru.educationalwork.developerslifegifs.R
import ru.educationalwork.developerslifegifs.presentation.viewmodel.GifViewModel

class MainActivity : AppCompatActivity() {

    companion object {
        const val CATEGORY_LATEST = "latest"
        const val CATEGORY_HOT = "hot"
        const val CATEGORY_TOP = "top"
        const val CATEGORY_RANDOM = "random"

        const val SHARED_PREFERENCES_NAME = "gif-pref"
        const val COUNTER_KEY = "counter key"

        var dbCounter: Int = 0
        var action: String = ""
        var page: Int = 0
        var itemCounter: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: GifViewModel = ViewModelProvider(this).get(GifViewModel::class.java)

        viewModel.getGif(CATEGORY_RANDOM, "0", 0)

        viewModel.gif.observe(this, Observer {gif ->
            setGifIntoView(gif.url)
        })

/*
        dbCounter = getSharedPreferences(
            SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(
            COUNTER_KEY, 0)
        if (dbCounter == 0) loadRandomPostFromNet()
        else loadPostFromDb()
*/

    }
/*
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(
            COUNTER_KEY,
            dbCounter
        ).apply()
    }

    fun onClickPreviousButton(view: View) {
        dbCounter++
        loadPostFromDb()
    }

    fun onClickNextButton(view: View) {
        imageButtonPrevious.isEnabled = true

        if (dbCounter == 0) loadRandomPostFromNet()
        else {
            dbCounter--
            loadPostFromDb()
        }

    }*/

  /*  private fun loadRandomPostFromNet() {
        val randomGifCall = App.instance?.apiService?.getRandomPost()

        progressBar.visibility = View.VISIBLE
        randomGifCall?.enqueue(object : Callback<GifItemResponse> {
            override fun onFailure(call: Call<GifItemResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, getString(R.string.network_error), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<GifItemResponse>, response: Response<GifItemResponse>) {
                if (response.isSuccessful) {
                    setGifIntoView(response.body()?.gifURL)

                    Executors.newSingleThreadExecutor().execute {
                        //val gifDbItem = DbGifModel(response.body()!!.id, response.body()!!.gifURL)
                        val gifDbItem = DbGifItem(url =  response.body()!!.gifURL)
                        Db.getInstance(this@MainActivity)?.getGifDao()?.addGif(gifDbItem)
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

        progressBar.visibility = View.VISIBLE
        specialGifCall?.enqueue(object: Callback<List<GifItem>>{
            override fun onFailure(call: Call<List<GifItem>>, t: Throwable) {

            }

            override fun onResponse(call: Call<List<GifItem>>, response: Response<List<GifItem>>) {
                val gif = response.body()?.get(itemCounter)
                setGifIntoView(gif.)
            }
        })

    }

    private fun loadPostFromDb() {
       val gifList = Db.getInstance(this)?.getGifDao()?.getAllGifs()
        if (gifList?.size == dbCounter) {
            imageButtonPrevious.isEnabled = false
            dbCounter--
            Log.d("TAGGG", dbCounter.toString())
            imageButtonPrevious.animate()
                .rotation(180f)
                .scaleX(0.5f)
                .scaleY(0.5f)
                .setDuration(1000)
                .start()
        } else {
            val gif = gifList?.get(gifList.size - 1 - dbCounter)
            setGifIntoView(gif?.url)
        }
    }
*/
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
                    R.id.action_latest -> action =
                        CATEGORY_LATEST
                    R.id.action_hot -> action =
                        CATEGORY_HOT
                    R.id.action_top -> action =
                        CATEGORY_TOP
                    R.id.action_random -> action =
                        CATEGORY_RANDOM
                }
                true
            }

        val bnView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnView.setOnNavigationItemSelectedListener(navListener)
    }

    fun resetData(){
        dbCounter = 0
        // и очистить БД
    }
}