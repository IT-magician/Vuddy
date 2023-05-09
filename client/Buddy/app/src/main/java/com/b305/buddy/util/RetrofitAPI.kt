package com.b305.buddy.util

import com.b305.buddy.service.AuthService
import com.b305.buddy.service.FeedService
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitAPI {
    private const val BASE_URL = "http://k8b305.p.ssafy.io:9001"
    
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
    
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
    
    val authService: AuthService by lazy {
        retrofit.create(AuthService::class.java)
    }

    val feedServie : FeedService by lazy {
        retrofit.create((FeedService::class.java))
    }

}
