package com.shubham.enamora.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "characters",
    indices = [
        Index(
            value = [
                "is_active",
                "sort_order"
            ]
        )
    ]
)
data class CharacterEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "display_name")
    val displayName: String,

    @ColumnInfo(name = "birth_date")
    val birthDate: String,

    @ColumnInfo(name = "pronouns")
    val pronouns: String,

    @ColumnInfo(name = "gender_identity")
    val genderIdentity: String,

    @ColumnInfo(name = "orientation")
    val orientation: String,

    @ColumnInfo(name = "city")
    val city: String,

    @ColumnInfo(name = "region")
    val region: String,

    @ColumnInfo(name = "country_code")
    val countryCode: String,

    @ColumnInfo(name = "occupation")
    val occupation: String,

    @ColumnInfo(name = "short_bio")
    val shortBio: String,

    @ColumnInfo(name = "profile_image_key")
    val profileImageKey: String,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "sort_order")
    val sortOrder: Int = 0,

    @ColumnInfo(name = "created_at")
    val createdAt: Long,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)