package com.runner.service


import com.runner.model.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiService {
    @Multipart
    @POST("login")
    fun login(
        @PartMap map: HashMap<String, RequestBody>?,
        @HeaderMap header: HashMap<String, String>
    ): Call<LoginModel?>?


    @GET("login/{id}")
    fun loginnew(
        @Path("id") id: String?,
        @HeaderMap header: HashMap<String, String>
    ): Call<LoginModelNew?>?

    @Multipart
    @POST("verifyPin")
    fun verifyPin(
        @PartMap map: HashMap<String, RequestBody>?,
        @HeaderMap header: HashMap<String, String>
    ): Call<VerifyPinModel?>?

    @GET("{api}/{id}")
    fun SearchUSer(
        @Path("api") api: String?,
        @Path("id") id: String?,
        @HeaderMap header: HashMap<String, String>
    ): Call<RedemptionListModel?>?

    @Multipart
    @POST("update")
    fun Allot(
        @PartMap map: HashMap<String, RequestBody>?,
        @HeaderMap header: HashMap<String, String>
    ): Call<VerifyResponce?>?

    @Multipart
    @POST("lunch_update")
    fun Allot1(
        @PartMap map: HashMap<String, RequestBody>?,
        @HeaderMap header: HashMap<String, String>
    ): Call<VerifyResponce?>?


    @Multipart
    @POST("add_guest")
    fun add_guest(
        @PartMap map: HashMap<String, RequestBody>?,
        @HeaderMap header: HashMap<String, String>
    ): Call<VerifyResponce?>?

    @GET("{api}/{id}/{type_qr}")
    fun SearchUSerk2mini(
        @Path("api") api: String?,
        @Path("id") id: String?,
        @HeaderMap header: HashMap<String, String>,
        @Path("type_qr") type_qr: String?
    ): Call<RedemptionListModel?>?

    @GET("{api}/{id}")
    fun UpdateToServer(
        @Path("api") api: String?,
        @Path("id") id: String?,
        @HeaderMap header: HashMap<String, String>
    ): Call<VerifyResponce?>?

    //ChooseCatgoryOption
    @GET("{api}/{id}")
    fun ChooseFormField(
        @Path("api") api: String?,
        @Path("id") id: String?,
        @HeaderMap header: HashMap<String, String>
    ): Call<ChooseCategoryModel?>?

    @GET("{api}/{loc}/{code}")
    fun Zapping(
        @Path("api") id: String?,
        @Path("loc") loc: String?,
        @Path("code") code: String?,
        @HeaderMap header: HashMap<String, String>
    ): Call<ZappingResponce?>?


    @GET("{api}/{loc}/{session}/{code}")
    fun Zappingwithsession(
        @Path("api") id: String?,
        @Path("loc") loc: String?,
        @Path("code") code: String?,
        @Path("session") session: String?,
        @HeaderMap header: HashMap<String, String>
    ): Call<ZappingResponce?>?


    @GET("{api}/{loc}/{code}")
    fun ZappingUpdate(
        @Path("api") id: String?,
        @Path("loc") loc: String?,
        @Path("code") code: String?,
        @HeaderMap header: HashMap<String, String>
    ): Call<VerifyResponce?>?

     @GET("zapping/{location}/{code}")
    fun Zapping(@Path("location") id: String?,@Path("code") code: String?,@HeaderMap header: HashMap<String, String>): Call<VerifyResponce?>?

    @Multipart
    @POST("{api}")
    fun SaveUser(
        @Path("api") api: String?,
        @PartMap map: HashMap<String, RequestBody>?,
        @HeaderMap header: HashMap<String, String>
    ): Call<OnspotModel?>?

    @Multipart
    @POST("{api}")
    fun EditUser(
        @Path("api") api: String?,
        @PartMap map: HashMap<String, RequestBody>?,
        @HeaderMap header: HashMap<String, String>
    ): Call<OnspotModel?>?

}