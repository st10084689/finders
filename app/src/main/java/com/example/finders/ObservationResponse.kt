package com.example.finders

data class ObservationResponse(
    val speciesCode: String,
    val comName: String,
    val sciName: String,
    val locId: String,
    val locName: String,
    val obsDt: String,
    val howMany: Int,
    val lat: Double,
    val lng: Double,
    val obsValid: Boolean,
    val obsReviewed: Boolean,
    val locationPrivate: Boolean,
    val subId: String
)