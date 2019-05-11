package com.cyclicsoft.com.common;

import com.cyclicsoft.com.remote.IGoogleAPI;
import com.cyclicsoft.com.remote.RetroClient;

public class Common {
    public static final String baseURL = "https://maps.googleapis.com";
    public static IGoogleAPI getGoogleApi(){
        return RetroClient.getClient(baseURL).create(IGoogleAPI.class);
    }
}
