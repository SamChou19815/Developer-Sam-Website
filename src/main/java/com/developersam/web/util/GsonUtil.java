package com.developersam.web.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * All Gson related operation helper class.
 */
public final class GsonUtil {
    
    /**
     * A default gson from Google as a reference.
     */
    private static final Gson DEFAULT_GSON = new Gson();
    /**
     * A default gson for the app.
     */
    public static final Gson GSON = defaultBuild().create();
    
    /**
     * Create a {@code GsonBuilder} of default configuration.
     *
     * @return a default {@code GsonBuilder}.
     */
    private static GsonBuilder defaultBuild() {
        return new GsonBuilder().setDateFormat(DateUtil.DATE_FORMAT);
    }
    
}
