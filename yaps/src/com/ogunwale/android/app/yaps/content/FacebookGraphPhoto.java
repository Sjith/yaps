/**
 * Copyright 2013 Olawale Ogunwale
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ogunwale.android.app.yaps.content;

import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;

/**
 * Provides a strongly-typed representation of the Photo as defined by the Graph
 * API.
 *
 * See: http://developers.facebook.com/docs/reference/api/photo/ for details
 *
 * Note that this interface is intended to be used with
 * {@link GraphObejct.Factory} and not implemented directly.
 *
 * @author ogunwale
 *
 */
public interface FacebookGraphPhoto extends GraphObject {

    /**
     * Returns the photo ID.
     *
     * @return the photo ID
     */
    public String getId();

    /**
     * Sets the photo ID.
     *
     * @param Id
     *            the Id of the photo
     */
    public void setId(String Id);

    // public ??? getFrom();
    // public void setFrom(???);

    // public ??? getTags();
    // public void setTags(???);

    /**
     * Returns the photo name.
     *
     * @return the photo name
     */
    public String getName();

    /**
     * Sets the name of the photo.
     *
     * @param name
     *            the name of the photo
     */
    public void setName(String name);

    // public ??? getNameTags();
    // public void setNameTags(???);

    /**
     * Returns the photo icon URL.
     *
     * @return the photo icon URL
     */
    public String getIcon();

    /**
     * Sets the photo icon URL.
     *
     * @param icon
     *            the photo icon URL
     */
    public void setIcon(String icon);

    /**
     * Returns the URL of the thumbnail-sized source of the photo.
     *
     * @return URL of the thumbnail-sized source of the photo
     */
    public String getPicture();

    /**
     * Set the URL of the thumbnail-sized soruce of the photo.
     *
     * @param picture
     *            the URL of the thumbnal-sized source of the photo
     */
    public void setPicture(String picture);

    /**
     * Returns the URL of the image source of the photo.
     *
     * @return URL of the image source of the photo
     */
    public String getSource();

    /**
     * Sets the URL of the image source of the photo.
     *
     * @param source
     *            the URL of the image source of the photo
     */
    public void setSource(String source);

    /**
     * Returns the height of the photo in pixels.
     *
     * @return height of photo in pixels
     */
    public int getHeight();

    /**
     * Sets the height of the photo in pixels.
     *
     * @param height
     *            the height of the photo in pixels
     */
    public void setHeight(int height);

    /**
     * Sets the width of the photo in pixels.
     *
     * @param width
     *            the width of the photo in pixels
     */
    public void setWidth(int width);

    /**
     * Returns the width of the photo in pixels.
     *
     * @return width of photo in pixels
     */
    public int getWidth();

    // public ??? getImages();
    // public void setImages(???);

    /**
     * Returns the link to the photo on Facebook.
     *
     * @return the link to the photo on Facebook
     */
    public String getLink();

    /**
     * Sets the link to the photo on Facebook.
     *
     * @param link
     *            the link to the photo on Facebook
     */
    public void setLink(String link);

    /**
     * Returns location information associated with the photo.
     *
     * @return Location information associated with the photo
     */
    public GraphPlace getPlace();

    /**
     * Sets the location information associated with the photo.
     *
     * @param place
     *            the location information associated with the photo
     */
    public void setPlace(GraphPlace place);

    /**
     * Returns the created time of the photo.
     *
     * @return the created time of the photo
     */
    public String getCreatedTime();

    /**
     * Sets the created time of the photo.
     *
     * @param createdTime
     *            the created time of the photo
     */
    public void setCreatedTime(String createdTime);

    /**
     * Returns the updated time of the photo.
     *
     * @return the updated time of the photo
     */
    public String getUpdatedTime();

    /**
     * Sets the updated time of the photo.
     *
     * @param updatedTime
     *            the updated time of the photo
     */
    public void setupdatedTime(String updatedTime);

    /**
     * Returns the position of the photo in the album.
     *
     * @return the position of the photo in the album
     */
    public int getPosition();

    /**
     * Sets the position of the photo in the album.
     *
     * @param position
     *            the position of the photo in the album
     */
    public void setPosition(int position);

}
