package com.dsheal.yummyspends.data.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleSheetsDto(
    @Json(name = "range")
    var range: String,
    @Json(name = "majorDimension")
    var majorDimension: String,
    @Json(name = "values")
    var values: List<ItemDto>)

data class ItemDto(
    val itemsList: List<String>
)
