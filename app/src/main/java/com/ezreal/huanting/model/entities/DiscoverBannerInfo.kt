package com.ezreal.huanting.model.entities

data class DiscoverBannerInfo(
    val encodeId: String,
    val exclusive: Boolean,
    val imageUrl: String,
    val scm: String,
    val targetId: Int,
    val targetType: Int,
    val titleColor: String,
    val typeTitle: String,
    val url: String
)