package com.runner.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class VerifyResponce {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
     var message: String? = null

}