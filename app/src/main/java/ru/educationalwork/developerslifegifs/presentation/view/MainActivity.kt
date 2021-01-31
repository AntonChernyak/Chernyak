package ru.educationalwork.developerslifegifs.presentation.view

import android.content.Context
import android.content.pm.ActivityInfo
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
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
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
        const val KEY_COUNTER = "counter key"
        const val KEY_CATEGORY = "category key"
        const val KEY_BN_ID = "bn id"

        const val ACTION_NEXT = "next"
        const val ACTION_PREVIOUS = "previous"
        const val ACTION_CHANGE_CATEGORY = "change"
    }

    private var category: String = CATEGORY_RANDOM
    private var counter: Int = 0

    private var lastClickTime: Long = 0
    private val viewModel: GifViewModel by lazy {
        ViewModelProvider(this@MainActivity).get(GifViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        bottomNavigationViewSettings()
        bottomNavigationView.selectedItemId = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(
            KEY_BN_ID, R.id.action_random)

        category = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getString(
            KEY_CATEGORY, CATEGORY_RANDOM).toString()

        counter = getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).getInt(
            KEY_COUNTER, 0)

        viewModelObserves()

        Log.d("TAGGG", "saveIn = $savedInstanceState")
        if (savedInstanceState == null) previousButtonOff()
    }


    private fun getData(action: String, counter: Int){
        viewModel.getGif(category, action, counter)
    }


    fun onClickNextButton(view: View) {
        // предотвращаем частое нажатие
        if (SystemClock.elapsedRealtime() - lastClickTime < 300) return
        lastClickTime = SystemClock.elapsedRealtime()

        imageButtonPrevious.isEnabled = true
        getData(ACTION_NEXT, counter)
    }

    fun onClickPreviousButton(view: View) {
        getData(ACTION_PREVIOUS, counter)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_BN_ID, bottomNavigationView.selectedItemId)
            .putString(KEY_CATEGORY, category)
            .apply()
    }

    private fun setGifIntoView(url: String?) {
        Glide.with(imageViewGif.context)
            .asGif()
            .load(url)
            .placeholder(R.drawable.image_search)
            .error(R.drawable.error_small)
            .listener(object : RequestListener<GifDrawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?,
                                          isFirstResource: Boolean): Boolean
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
            .transition(DrawableTransitionOptions.withCrossFade())
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
                getData(ACTION_CHANGE_CATEGORY, counter)
                true
            }
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener)

    }

    private fun previousButtonOff(){
        imageButtonPrevious.isEnabled = false
        progressBar.visibility = View.GONE

        imageButtonPrevious.animate()
            .rotation(180f)
            .setDuration(1000)
            .start()
        imageButtonPrevious.setColorFilter(R.color.white)
    }

    private fun previousButtonOn(){
        imageButtonPrevious.isEnabled = true
        imageButtonPrevious.animate()
            .rotation(0f)
            .setDuration(1000)
            .start()
        imageButtonPrevious.clearColorFilter()
    }

    private fun viewModelObserves(){
        viewModel.gif.observe(this, Observer { gif ->
            setGifIntoView(gif.url)
            textViewDescription.text = gif.description
        })

        viewModel.isLoading.observe(this, Observer { isLoading ->
            if (isLoading) progressBar.visibility = View.VISIBLE
        })

        viewModel.error.observe(this, Observer {error ->
            Toast.makeText(this, "Ошибка", Toast.LENGTH_LONG).show()
            textViewDescription.text = error
            progressBar.visibility = View.GONE

            Glide.with(imageViewGif.context).load(R.drawable.error_large).into(imageViewGif)
        })

        viewModel.isLast.observe(this, Observer { isLast ->
            if (isLast) previousButtonOff()
            else previousButtonOn()
        })

        viewModel.backStackCounter.observe(this, Observer {counter ->
            getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
                .edit()
                .putInt(KEY_COUNTER, counter)
                .apply()
        })
    }

}