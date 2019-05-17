package suncor.com.android.mfp;

public class ErrorCodes {

    //A general error which can't be mapped to server errors
    public final static String GENERAL_ERROR = "SUNCOR000";


    //Copied from https://bitbucket.org/rfmp-suncor/suncor-mfp/src/master/src/main/java/com/suncor/adapters/constants/SuncorConstants.java
    public static final String ERR_DB_CONNECTION_ERROR_CODE = "SUNCOR001";
    public static final String ERR_INTERNAL_SERVER_ERROR_CODE = "SUNCOR002";
    public static final String ERR_INVALID_PARAM_ERROR_CODE = "SUNCOR004";

    public static final String ERR_PROFILE_NOT_FOUND = "SUNCOR005";
    public static final String ERR_CONFLICTING_LOGINS = "SUNCOR006";
    public static final String ERR_INVALID_ENCRYPTION_VALUE_CODE = "SUNCOR007";
    public static final String ERR_INVALID_DECRYPTION_VALUE_CODE = "SUNCOR008";
    public static final String ERR_ACCOUNT_HARD_LOCK = "SUNCOR009";
    public static final String ERR_ACCOUNT_SOFT_LOCK = "SUNCOR010";

    public static final String ERR_PASSWORD_CHANGE_REQUIRED = "SUNCOR012";
    public static final String ERR_ACCOUNT_BAD_PASSWORD = "SUNCOR013";
    public static final String ERR_ACCOUNT_NOT_FOUND = "SUNCOR013";
    public static final String ERR_PUBWEB_SERVICE_ERROR_CODE = "SUNCOR014";
    public static final String ERR_PASSWORD_DUPLICATED = "SUNCOR015";
    public static final String ERR_INVALID_CARD_ERROR_CODE = "SUNCOR016";
    public static final String ERR_ACCOUNT_ALREDY_REGISTERED_ERROR_CODE = "SUNCOR017";
    public static final String ERR_USER_INFO_NOT_MATCHED = "SUNCOR018";

    public static final String ERR_LIKING_CARD_FAILED = "SUNCOR020";
}
