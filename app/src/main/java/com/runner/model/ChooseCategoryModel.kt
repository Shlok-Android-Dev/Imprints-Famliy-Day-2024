package com.runner.model

data class ChooseCategoryModel(
    val form_fields: List<FormField>,
    val message: String,
    val status: Boolean
)
{
    data class FormField(
        val created_at: String,
        val field_name: String,
        val field_title: String,
        val field_type: String,
        val required: Int,
        val updated_at: String,
        val value: String
    )
}
