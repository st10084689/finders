package com.example.finders
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface EBirdApi {
    @GET("/v2/data/obs/ZA/recent/")
    fun getObservations(): Call<List<ObservationResponse>>
}