package com.engineer.android.mini.net.model;

import androidx.annotation.NonNull;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.List;

public class SiteWrapper {

    private List<Site> sites;

    @JSONField(serialize = false)
    private String token;

    public List<Site> getSites() {
        return sites;
    }

    @JSONField(deserialize = false)
    public void setSites(List<Site> sites) {
        this.sites = sites;
    }

    public String getToken() {
        return token;
    }

    //    @JSONField(deserialize = true)
    public void setToken(String token) {
        this.token = token;
    }

    @NonNull
    @Override
    public String toString() {
        return "SiteWrapper{"
                + "sites=" + sites
                + ", token=" + token + "}";
    }

    public static class Site {
        public String id;
        public String name;
        public String url;

        public Site() {
        }

        public Site(String id, String name, String url) {
            this.id = id;
            this.name = name;
            this.url = url;
        }
    }
}
