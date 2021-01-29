package ru.educationalwork.developerslifegifs

import android.app.Application
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.educationalwork.developerslifegifs.api.ApiService

class App : Application() {
    lateinit var apiService: ApiService

    override fun onCreate() {
        super.onCreate()
        instance = this
        initRetrofit()
    }

    private fun initRetrofit() {
        // Подключаем интерцептор для логгирования
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.HEADERS

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        // инициализация Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            // .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        // создаём реализацию
        apiService = retrofit.create(ApiService::class.java)

    }

    companion object {
        const val BASE_URL = "https://developerslife.ru/"

        var instance: App? = null
            private set
    }
}