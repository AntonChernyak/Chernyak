package ru.educationalwork.developerslifegifs

import android.app.Application
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.educationalwork.developerslifegifs.domain.GifInteractor
import ru.educationalwork.developerslifegifs.repository.server.ApiService
import ru.educationalwork.developerslifegifs.repository.database.Db
import ru.educationalwork.developerslifegifs.repository.database.GifRepositoryInterface
import ru.educationalwork.developerslifegifs.repository.database.Repository
import java.util.concurrent.Executors

class App : Application() {
    lateinit var apiService: ApiService
    lateinit var gifInteractor: GifInteractor
    lateinit var repository : GifRepositoryInterface

    override fun onCreate() {
        super.onCreate()
        instance = this
        initRetrofit()
        initDb()
        repository = Repository(Db.getInstance(this)?.getGifDao()!!)
        initInteractor()
    }

    private fun initRetrofit() {
        // Подключаем интерцептор для логгирования
/*        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.HEADERS*/

        val okHttpClient = OkHttpClient.Builder()
            //.addInterceptor(logging)
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

    private fun initDb(){
        Executors.newSingleThreadScheduledExecutor().execute {
            Db.getInstance(this)
        }
    }

    private fun initInteractor() {
        gifInteractor = GifInteractor(apiService, repository)
    }

    companion object {
        const val BASE_URL = "https://developerslife.ru/"
        const val DATABASE_NAME = "gif-db.db"

        var instance: App? = null
            private set
    }

}