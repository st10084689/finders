package com.example.finders

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import okhttp3.Interceptor
import okhttp3.OkHttpClient

class EBirdService {

  private val api: EBirdApi

  init {
    val httpClient = OkHttpClient.Builder().apply {
      addInterceptor(Interceptor { chain ->
        val original = chain.request()
        val requestBuilder = original.newBuilder()

          .header("x-ebirdapitoken", "")//TODO: Add Ebird api key

        val request = requestBuilder.build()
        chain.proceed(request)
      })
    }.build()

    val retrofit = Retrofit.Builder()
      .baseUrl("https://api.ebird.org/")
      .addConverterFactory(GsonConverterFactory.create())
      .client(httpClient) // Set the custom OkHttpClient
      .build()

    api = retrofit.create(EBirdApi::class.java)
  }

  fun getObservations(): Call<List<ObservationResponse>> {
    return api.getObservations()
  }
}
