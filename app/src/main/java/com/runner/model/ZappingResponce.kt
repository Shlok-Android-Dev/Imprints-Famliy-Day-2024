package com.runner.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class ZappingResponce {
    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
     var message: String? = null

    @SerializedName("data")
    @Expose
     var data: Data? = null

    class Data {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("user_type_id")
        @Expose
        var userTypeId: Any? = null

        @SerializedName("ticket_id")
        @Expose
        var ticketId: Int? = null

        @SerializedName("order_id")
        @Expose
        var orderId: String? = null

        @SerializedName("master_user")
        @Expose
        var masterUser: Int? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("last_name")
        @Expose
        var lastName: Any? = null

        @SerializedName("email")
        @Expose
        var email: String? = null

        @SerializedName("country_code")
        @Expose
        var countryCode: String? = null

        @SerializedName("phone")
        @Expose
        var phone: String? = null

        @SerializedName("country")
        @Expose
        var country: String? = null

        @SerializedName("state")
        @Expose
        var state: Any? = null

        @SerializedName("city")
        @Expose
        var city: Any? = null

        @SerializedName("organization")
        @Expose
        var organization: String? = null

        @SerializedName("designation")
        @Expose
        var designation: Any? = null

        @SerializedName("festival_dates")
        @Expose
        var festivalDates: String? = null

        @SerializedName("organization_role")
        @Expose
        var organizationRole: Any? = null

        @SerializedName("job_level")
        @Expose
        var jobLevel: Any? = null

        @SerializedName("image_url")
        @Expose
        var imageUrl: Any? = null

        @SerializedName("unique_code")
        @Expose
        var uniqueCode: String? = null

        @SerializedName("qrcode_path")
        @Expose
        var qrcodePath: Any? = null

        @SerializedName("eticket_path")
        @Expose
        var eticketPath: String? = null

        @SerializedName("meta_data")
        @Expose
        var metaData: Any? = null

        @SerializedName("status")
        @Expose
        var status: Int? = null

        @SerializedName("email_status")
        @Expose
        var emailStatus: Int? = null

        @SerializedName("is_printed")
        @Expose
        var isPrinted: Int? = null

        @SerializedName("printed_at")
        @Expose
        var printedAt: Any? = null

        @SerializedName("send_message")
        @Expose
        var sendMessage: Int? = null

        @SerializedName("utm_source")
        @Expose
        var utmSource: Any? = null

        @SerializedName("utm_medium")
        @Expose
        var utmMedium: Any? = null

        @SerializedName("utm_campaign")
        @Expose
        var utmCampaign: Any? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null
    }

}