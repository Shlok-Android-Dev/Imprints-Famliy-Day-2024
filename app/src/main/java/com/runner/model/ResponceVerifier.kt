package com.runner.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
class ResponceVerifier {
    @SerializedName("success")
    @Expose
    var success: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

}