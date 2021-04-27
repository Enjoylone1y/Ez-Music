package com.ezreal.huanting.view

import com.ezreal.huanting.model.entities.DiscoverBannerInfo

interface DiscoverView : BaseView {
    fun onShowBanner( banners:List<DiscoverBannerInfo> )
}