package com.ogunwale.android.app.yaps.content;

/**
 * Listener interface for GetPicasaDataTimerTask. Listener is called to report
 * status and data collected.
 *
 * @author ogunwale
 *
 */
public interface PicasaDataListener {

    /**
     * Cause of request failure.
     *
     * @author ogunwale
     *
     */
    // @formatter:off
    public static enum FailureCause {
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

        public static String toString(FailureCause cause) {
            String string = "???";

            switch(cause) {
            case ACCOUNT_NAME_NULL:
                string = "Account Name is null";
                break;
            case AUTHENTICATOR_EXCEPTION:
                string = "Authenticator exception";
                break;
            case AUTH_BUNDLE_NULL:
                string = "Auth bundle is null";
                break;
            case AUTH_IO_EXCEPTION:
                string = "Auth IO exception";
                break;
            case AUTH_TOKEN_NULL:
                string = "Auth token null";
                break;
            case HTTP_401_UNAUTHORIZED:
                string = "Http 401 unauthorized";
                break;
            case HTTP_403_TOKEN_PROBLEM:
                string = "Http 403 token problem";
                break;
            case HTTP_RESPONSE_EXCEPTION:
                string = "Http response exception";
                break;
            case INVALID_REQUEST:
                string = "Invalid request";
                break;
            case IO_EXCEPTION:
                string = "IO exception";
                break;
            case OPERATION_CANCELED_EXCEPTION:
                string = "Operation canceled exception";
                break;
            case UNKNOWN:
                string = "Unknown";
                break;
            }

            return(string);
        }
    }
    // @formatter:on

    /**
     * Method is called when data request from the Picasa account fails.
     */
    void RequestFailed(FailureCause cause);

    /**
     * Method is called when the data request is fully complete without errors.
     * {@link RequestFailed} method is called instead when a failure occurs.
     */
    void RequestComplete();

}
