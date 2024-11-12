package com.runner.model

data class LginModelNewest(
    val categories: List<Category>,
    val `data`: Data,
    val message: String,
    val status: Boolean
) {
    data class Category(
        val category: String,
        val id: Int,
        val name: String
    )

    data class Data(
        val use_data: UseData,
        val zapping_locations: List<ZappingLocation>
    )

    data class UseData(
        val category: String,
        val created_at: String,
        val id: Int,
        val role_id: Int,
        val updated_at: String,
        val username: String
    )

    data class ZappingLocation(
        val location: String,
        val session: List<String>
    )
}
