package com.runner

import android.content.Context
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import com.google.gson.GsonBuilder
import com.runner.service.ApiService
import com.runner.service.CustomInterceptor
import okhttp3.Cache
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class RuheApp : MultiDexApplication() {
    var isAidl = false
    var apiService: ApiService? = null
        private set
    override fun onCreate() {
        super.onCreate()
        instance = this
        createFunction()
        MultiDex.install(applicationContext)
        createApiService()
        val builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

    }

    public fun createFunction() {
        cachedThreadPool = Executors.newCachedThreadPool()
    }

    var cachedThreadPool: ExecutorService? = null

    @JvmName("getCachedThreadPool1")
    fun getCachedThreadPool(): ExecutorService? {
        return cachedThreadPool
    }


    private val handler = Handler()

    fun getHandler(): Handler? {
        return handler
    }
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
    }
    fun createApiService(): ApiService? {
        val gson = GsonBuilder().create()
        val httpCacheDirectory = File(cacheDir, "cache_file")
        val cache = Cache(httpCacheDirectory, 20 * 1024 * 1024)

        // Add HttpLoggingInterceptor for logging
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY// Log body level to see request/response details
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }


        val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .connectionPool(ConnectionPool(0, 5 * 60 * 1000, TimeUnit.SECONDS))
            .addInterceptor(loggingInterceptor)  // Logging interceptor added here
            .addInterceptor(CustomInterceptor(instance, Locale.getDefault().language, appVersion))  // Custom interceptor
                .cache(cache)
                .build()

        val prefeMain = getSharedPreferences("FRIENDS", MODE_PRIVATE)

        var retrofit: Retrofit?=null


        prefeMain.getString("islogged_in","")?.let { Log.e("@@ISLoggedoooo", it) }
        if(prefeMain.getString("islogged_in","").equals("")){
             retrofit = Retrofit.Builder().client(okHttpClient)
                .baseUrl(getString(R.string.URL_APP))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }else {
             retrofit = Retrofit.Builder().client(okHttpClient)
                .baseUrl(getString(R.string.URL_APP))
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
        }


        apiService = retrofit.create(ApiService::class.java)
        return apiService
    }
    private val appVersion: String
        private get() = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: Exception) {
            e.printStackTrace()
            "1.1"
        }
    companion object {
        var instance: RuheApp? = null

    }
}