package com.engineer.android.mini.net.model

data class Site(var id: String, var name: String, var url: String)
data class SiteWrapper(var token: String, var sites: List<Site>, var key: String?)