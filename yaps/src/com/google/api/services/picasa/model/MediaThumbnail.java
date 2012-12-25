package com.google.api.services.picasa.model;

import com.google.api.client.util.Key;

/**
 * @author ogunwale
 *
 */
public class MediaThumbnail {

    @Key("@height")
    public String height;
    
    @Key("@width")
    public String width;

    @Key("@url")
    public String url;

}
