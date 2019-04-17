package kr.ho1.poopee.common.http

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private const val READ_TIMEOUT = 30
    private const val CONNECT_TIMEOUT = 30

    private var retrofit: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            val okHttpClient = OkHttpClient.Builder()
                    .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                    .build()

            retrofit = Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient)
                    .build()
        }
        return retrofit!!
    }

}