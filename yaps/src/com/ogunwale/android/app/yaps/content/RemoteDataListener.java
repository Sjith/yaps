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
