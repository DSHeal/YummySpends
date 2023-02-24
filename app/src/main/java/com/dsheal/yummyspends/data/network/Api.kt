package com.dsheal.yummyspends.data.network

import com.dsheal.yummyspends.data.dto.GoogleSheetsDto
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("/v4/spreadsheets/{sheetId}/values/{range}?key={apiKey}")
    fun getAllTableData(
        @Path("sheetId") sheetId: String,
        @Path("range") range: String,
        @Path("apiKey") apiKey: String
    ): GoogleSheetsDto
}