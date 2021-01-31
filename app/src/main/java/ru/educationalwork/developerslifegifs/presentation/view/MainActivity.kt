package ru.educationalwork.developerslifegifs.presentation.view

import android.content.Context
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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

        private var dbCounter: Int = 0
    }

    private var category: String = ""
    private var page: Int = 0
    private var itemCounter: Int = 0

    private var lastClickTime: Long = 0
    private val viewModel: GifViewModel by lazy {
        ViewModelProvider(this@MainActivity).get(GifViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //bottomNavigationView.menu.getItem(0).isChecked = true
        bottomNavigationViewSettings()
        bottomNavigationView.selectedItemId = R.id.action_random


        viewModel.gif.observe(this, Observer { gif ->
            setGifIntoView(gif.url)
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) progressBar.visibility = View.VISIBLE
        })

        viewModel.isLast.observe(this, Observer { isLast ->
            if (isLast) {
                imageButtonPrevious.isEnabled = false
                dbCounter--
                progressBar.visibility = View.GONE
                Log.d("TAGGG", dbCounter.toString())
                imageButtonPrevious.animate()
                    .rotation(180f)
                    .scaleX(0.5f)
                    .scaleY(0.5f)
                    .setDuration(1000)
                    .start()
            }
        })


        dbCounter = getSharedPreferences(
            SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE
        ).getInt(
            COUNTER_KEY, 0
        )
        if (dbCounter == 0) getDataFromNet(viewModel)
        else getDataFromDb(viewModel)

        Log.d("TAGGG", "onCreate = ${dbCounter}")

    }


    private fun getDataFromNet(viewModel: GifViewModel) {
        // Log.d("TAGGG", "$category, $page, $itemCounter")
        viewModel.getGifFromNet(category, page.toString(), itemCounter)
    }

    private fun getDataFromDb(viewModel: GifViewModel) {
        viewModel.getGifFromDb(dbCounter)
    }


    fun onClickNextButton(view: View) {
        // предотвращаем частое нажатие
        if (SystemClock.elapsedRealtime() - lastClickTime < 300) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()

        // getDataFromNet(viewModel)
        imageButtonPrevious.isEnabled = true

        Log.d("TAGGG", "onClickNext1 = ${dbCounter}")
        if (dbCounter == 0) getDataFromNet(viewModel)
        else {
            dbCounter--
            Log.d("TAGGG", "onClickNext2 = ${dbCounter}")
            getDataFromDb(viewModel)
        }


    }

    fun onClickPreviousButton(view: View) {
        dbCounter++
        Log.d("TAGGG", "onClickPrevious = ${dbCounter}")
        getDataFromDb(viewModel)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit().putInt(
            COUNTER_KEY,
            dbCounter
        ).apply()
    }

    private fun setGifIntoView(url: String?) {
        Glide.with(imageViewGif.context)
            .asGif()
            .load(url)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?
                                          , isFirstResource: Boolean): Boolean
                {
                    Toast.makeText(this@MainActivity,getString(R.string.error) + e.toString(), Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                    return false
                }

                override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?,
                                             dataSource: DataSource?, isFirstResource: Boolean): Boolean
                {
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
                    R.id.action_latest -> category = CATEGORY_LATEST
                    R.id.action_hot -> category = CATEGORY_HOT
                    R.id.action_top -> category = CATEGORY_TOP
                    R.id.action_random -> category = CATEGORY_RANDOM
                }
                true
            }

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener)
    }

    fun resetData() {
        dbCounter = 0
        // и очистить БД
    }

}