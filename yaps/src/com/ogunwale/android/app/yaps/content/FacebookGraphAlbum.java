package com.ogunwale.android.app.yaps.content;

import com.facebook.model.GraphObject;

/**
 * Provides a strongly-typed representation of the Album as defined by the Graph
 * API.
 *
 * See http://developers.facebook.com/docs/reference/api/album/ for details
 *
 * Note that this interface is intended to be used with GraphObject.Factory and
 * not implemented directly.
 *
 * @author ogunwale
 *
 */
public interface FacebookGraphAlbum extends GraphObject {

    public String getId();

    public void setId(String Id);

    // public ??? getFrom();
    // public void setFrom(???);

    public String getName();

    public void setName(String name);

    public String getDescription();

    public void setDescription(String description);

    public String getLocation();

    public void setLocation(String location);

    public String getLink();

    public void setLink(String link);

    public String getCoverPhoto();

    public void setCoverPhoto(String coverPhoto);

    public String getPrivacy();

    public void setPrivacy(String privacy);

    public String getCount();

    public void setCount(String count);

    public String getType();

    public void setType(String type);

    public String getCreatedTime();

    public void setCreatedTime(String createdTime);

    public String getUpdatedTime();

    public void setUpdatedTime(String updatedTime);

    public boolean getCanUpload();

    public void setCanUpload(boolean canUpload);
}
