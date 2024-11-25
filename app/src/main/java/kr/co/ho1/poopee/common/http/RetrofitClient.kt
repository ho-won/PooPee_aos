package kr.co.ho1.poopee.common.http

import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import kr.co.ho1.poopee.common.ObserverManager.context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


object RetrofitClient {
    private const val READ_TIMEOUT = 30
    private const val CONNECT_TIMEOUT = 30

    private var retrofit: Retrofit? = null
    private var retrofitKaKao: Retrofit? = null

    fun getClient(baseUrl: String): Retrofit {
        if (retrofit == null) {
            val cookieJar = PersistentCookieJar(SetCookieCache(), SharedPrefsCookiePersistor(context))

            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .cookieJar(cookieJar)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }

        return retrofit!!
    }

    fun getClientKaKao(baseUrl: String): Retrofit {
        if (retrofitKaKao == null) {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .connectTimeout(CONNECT_TIMEOUT.toLong(), TimeUnit.SECONDS)
                .addInterceptor { chain ->
                    val newRequest = chain.request().newBuilder()
                        .addHeader("Authorization", "KakaoAK " + RetrofitService.KAKAO_API_KEY)
                        .build()
                    chain.proceed(newRequest)
                }
                .build()

            retrofitKaKao = Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        }
        return retrofitKaKao!!
    }

}