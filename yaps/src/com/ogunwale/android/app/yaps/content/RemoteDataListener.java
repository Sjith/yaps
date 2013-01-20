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

/**
 * Listener interface for {@link GetRemoteDataTimerTask}. Listener is called to
 * report status and data collected.
 *
 * @author ogunwale
 *
 */
public interface RemoteDataListener {

    /**
     * Status of request.
     *
     * @author ogunwale
     *
     */
    // @formatter:off
    public static enum Status {
        SUCCESSFUL,
        AUTH_TOKEN_NULL,
        ACCOUNT_NAME_NULL,
        AUTH_BUNDLE_NULL,
        OPERATION_CANCELED_EXCEPTION,
        AUTHENTICATOR_EXCEPTION,
        AUTH_IO_EXCEPTION,
        HTTP_RESPONSE_EXCEPTION,
        HTTP_401_UNAUTHORIZED,
        HTTP_403_TOKEN_PROBLEM,
        IO_EXCEPTION,
        INVALID_REQUEST,
        UNKNOWN;
    }
    // @formatter:on

    /**
     * Method is called on completion of the data request.
     *
     * @param status
     *            the status of the data request
     */
    void RequestComplete(Status status);

}
