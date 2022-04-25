package com.engineer.android.mini.net.model

import com.alibaba.fastjson.annotation.JSONField

data class Site(var id: String, var name: String, var url: String)
class SiteWrapper1 {
    var token: String = ""
    var sites: List<Site>? = null
    var simple: Simple? = null

    @JSONField(serialize = false)
    var key: String? = ""
    override fun toString(): String {
        return "SiteWrapper(token='$token', sites=$sites, simple=$simple, key=$key)"
    }


}


class Simple {
    @JSONField(serialize = false)
    var age: Int? = 0

    override fun toString(): String {
        return "Simple(age=$age)"
    }


}