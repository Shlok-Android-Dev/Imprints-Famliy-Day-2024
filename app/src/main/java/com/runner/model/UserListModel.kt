package com.runner.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
class UserListModel {
    @SerializedName("success")
    @Expose
     val success: Boolean? = null

    @SerializedName("message")
    @Expose
     val message: String? = null

    @SerializedName("Output")
    @Expose
     val output: ArrayList<Output>? = null


    public class Output {
        @SerializedName("userId")
        @Expose
         val userId: Int? = null

        @SerializedName("name")
        @Expose
         val name: String? = null

        @SerializedName("email")
        @Expose
         val email: String? = null

    }
}