package suncor.com.android.mfp;

public class ErrorCodes {

    //A general error which can't be mapped to server errors
    public final static String GENERAL_ERROR = "SUNCOR000";

    public final static String OTHER_SESSION_STARTED = "SUNCOR006";
    public final static String ACCOUNT_LOCKED = "SUNCOR009";
    public static final String EXISTING_EMAIL = "SUNCOR017";


    //Copied from https://bitbucket.org/rfmp-suncor/suncor-mfp/src/master/src/main/java/com/suncor/adapters/constants/SuncorConstants.java
    public static final String DB_CONNECTION_ERROR_CODE = "SUNCOR001";
    public static final String INTERNAL_SERVER_ERROR_CODE = "SUNCOR002";
    public static final String INVALID_LOCATION_COORDINATES_ERROR_CODE = "SUNCOR003";
    public static final String INVALID_PARAM_ERROR_CODE = "SUNCOR004";
    public static final String PROFILE_NOT_FOUND = "SUNCOR005";
    public static final String CONFLICTING_LOGINS = "SUNCOR006";
    public static final String INVALID_ENCRYPTION_VALUE_CODE = "SUNCOR007";
    public static final String INVALID_DECRYPTION_VALUE_CODE = "SUNCOR008";
    public static final String PUBWEB_SERVICE_ERROR_CODE = "SUNCOR014";
    public static final String INVALID_CARD_ERROR_CODE = "SUNCOR016";
    public static final String ACCOUNT_ALREDY_REGISTERED_ERROR_CODE = "SUNCOR017";
}
