package com.runner.model
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class LoginModel {
    @SerializedName("status")
    @Expose
    private var status: Boolean? = null

    @SerializedName("message")
    @Expose
    private var message: String? = null

    @SerializedName("data")
    @Expose
    private var data: Data? = null

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

    fun getData(): Data? {
        return data
    }

    fun setData(data: Data?) {
        this.data = data
    }


    class Data {
        @SerializedName("id")
        @Expose
        private var id: Int? = null

        @SerializedName("event_id")
        @Expose
        private var eventId: Int? = null

        @SerializedName("role_id")
        @Expose
        private var roleId: Int? = null

        @SerializedName("name")
        @Expose
        private var name: String? = null

        @SerializedName("phone")
        @Expose
        private var phone: String? = null

        @SerializedName("created_at")
        @Expose
        private var createdAt: String? = null

        @SerializedName("updated_at")
        @Expose
        private var updatedAt: String? = null

        fun getId(): Int? {
            return id
        }

        fun setId(id: Int?) {
            this.id = id
        }

        fun getEventId(): Int? {
            return eventId
        }

        fun setEventId(eventId: Int?) {
            this.eventId = eventId
        }

        fun getRoleId(): Int? {
            return roleId
        }

        fun setRoleId(roleId: Int?) {
            this.roleId = roleId
        }

        fun getName(): String? {
            return name
        }

        fun setName(name: String?) {
            this.name = name
        }

        fun getPhone(): String? {
            return phone
        }

        fun setPhone(phone: String?) {
            this.phone = phone
        }

        fun getCreatedAt(): String? {
            return createdAt
        }

        fun setCreatedAt(createdAt: String?) {
            this.createdAt = createdAt
        }

        fun getUpdatedAt(): String? {
            return updatedAt
        }

        fun setUpdatedAt(updatedAt: String?) {
            this.updatedAt = updatedAt
        }
    }






}