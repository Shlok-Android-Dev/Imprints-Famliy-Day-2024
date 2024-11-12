package com.runner.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName
import java.util.Objects


class VerifyPinModel {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("event_data")
    @Expose
    private var eventData: EventData? = null

    @SerializedName("settings_data")
    @Expose
     var settingsData: Object? = null


    @SerializedName("form_settings")
    @Expose
     var formSettings: List<FormSetting>? = null




    fun getStatus(): Boolean? {
        return status
    }

    fun setStatus(status: Boolean?) {
        this.status = status
    }

    fun getMessage(): String? {
        return message
    }

    fun setMessage(message: String?) {
        this.message = message
    }

    fun getEventData(): EventData? {
        return eventData
    }

    fun setEventData(eventData: EventData?) {
        this.eventData = eventData
    }






    class EventData {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("is_cashless")
        @Expose
        var is_cashless: Int? = 0

        @SerializedName("prefix_uniquecode")
        @Expose
        var prefixUniquecode: String? = null

        @SerializedName("name")
        @Expose
        var name: String? = null

        @SerializedName("title")
        @Expose
        var title: String? = null

        @SerializedName("logo")
        @Expose
        var logo: String? = null

        @SerializedName("event_address")
        @Expose
        var eventAddress: String? = null

        @SerializedName("base_url")
        @Expose
        var baseUrl: String? = null

        @SerializedName("pin")
        @Expose
        var pin: Int? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null
    }

    class SettingsData {
        @SerializedName("locations")
        @Expose
        var locations: String? = null
    }

    class FormSetting {
        @SerializedName("id")
        @Expose
        var id: Int? = null

        @SerializedName("event_id")
        @Expose
        var eventId: Int? = null

        @SerializedName("field_title")
        @Expose
        var fieldTitle: String? = null

        @SerializedName("field_name")
        @Expose
        var fieldName: String? = null

        @SerializedName("field_type")
        @Expose
        var fieldType: String? = null

        @SerializedName("required")
        @Expose
        var required: Int? = null

        @SerializedName("value")
        @Expose
        var value: String? = null

        @SerializedName("created_at")
        @Expose
        var createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        var updatedAt: String? = null
    }


}