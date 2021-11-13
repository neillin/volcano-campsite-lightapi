package com.mservicetech.campsite;

import java.util.HashMap;
import java.util.Map;

public class ApplicationConfig {
    public static final String CONFIG_NAME = "app-config";

    private Map<String, String> queryMap = new HashMap<>();


    public ApplicationConfig() {
    }

    public Map<String, String> getQueryMap() {
        return queryMap;
    }

    public void setQueryMap(Map<String, String> queryMap) {
        this.queryMap = queryMap;
    }
}
