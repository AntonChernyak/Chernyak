package ru.educationalwork.developerslifegifs

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
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
import ru.educationalwork.developerslifegifs.model.random_model.GifItemRandomResponse

class MainActivity : AppCompatActivity() {

    companion object {
        const val ACTION_LATEST = "latest"
        const val ACTION_HOT = "hot"
        const val ACTION_TOP = "top"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getRandomPost()
    }

    fun onClickPreviousButton(view: View) {
        // Грузим url из кеша прямо в Glide
    }

    fun onClickNextButton(view: View) {
        getRandomPost()
    }

    private fun getRandomPost() {
        val randomGifCall = App.instance?.apiService?.getRandomPost()

        progressBar.visibility = View.VISIBLE
        randomGifCall?.enqueue(object : Callback<GifItemRandomResponse> {
            override fun onFailure(call: Call<GifItemRandomResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, getString(R.string.network_error), Toast.LENGTH_LONG).show()
                progressBar.visibility = View.GONE
            }

            override fun onResponse(call: Call<GifItemRandomResponse>, response: Response<GifItemRandomResponse>) {
                if (response.isSuccessful) {
                    loadGifIntoView(response)
                } else {
                    Toast.makeText(this@MainActivity, getString(R.string.error) + response.code(), Toast.LENGTH_LONG).show()
                    progressBar.visibility = View.GONE
                }
            }
        })
    }

    fun loadGifIntoView(response: Response<GifItemRandomResponse>) {
        Glide.with(imageViewGif.context)
            .asGif()
            .load(response.body()?.gifURL)
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

/*    private fun bottomNavigationViewSettings() {

        val navListener: BottomNavigationView.OnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_latest -> dsd;
                    R.id.action_hot -> sad
                    R.id.action_top ->
                }
                true
            }

        val bnView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bnView.setOnNavigationItemSelectedListener(navListener)
    }*/

}