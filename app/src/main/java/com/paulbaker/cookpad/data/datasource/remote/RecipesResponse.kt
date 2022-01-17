package com.paulbaker.cookpad.data.datasource.remote


import com.google.gson.annotations.SerializedName
import com.paulbaker.cookpad.data.datasource.local.Data

data class RecipesResponse(
    @SerializedName("data")
    val data: List<Data>?,
    @SerializedName("success")
    val success: Boolean?
) {

}