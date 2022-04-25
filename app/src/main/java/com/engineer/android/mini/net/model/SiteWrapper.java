package com.engineer.android.mini.net.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class SiteWrapper {

    @JSONField(deserialize = false)
    public List<Site> sites;

    @JSONField(deserialize = false)
    public String token;

    @Override
    public String toString() {
        return "SiteWrapper{" +
                "sites=" + sites +
                ", token='" + token + '\'' +
                '}';
    }
}
