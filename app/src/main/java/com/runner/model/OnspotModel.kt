package com.runner.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class OnspotModel {
    @SerializedName("status")
     var status: Boolean? = null

    @SerializedName("message")
    @Expose
     var message: String? = null



    @SerializedName("data")
    @Expose
    var data: Object? = null

}