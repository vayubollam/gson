package suncor.com.android.mfp;

public class SigninResponse {
    private Status status;
    private int remainingAttempts;
    private int timeOut;
    private String additionalData;

    private SigninResponse(Status status, int data) {
        this.status = status;
        switch (status) {
            case SUCCESS:
            case HARD_LOCKED:
                this.remainingAttempts = -1;
                this.timeOut = -1;
                break;
            case WRONG_CREDENTIALS:
                this.remainingAttempts = data;
                this.timeOut = -1;
                break;
            case SOFT_LOCKED:
                this.timeOut = data;
                this.remainingAttempts = -1;
                break;
            default:
                //do nothing
        }
    }

    public static SigninResponse success() {
        return new SigninResponse(Status.SUCCESS, -1);
    }

    public static SigninResponse wrongCredentials(int remainingAttempts) {
        return new SigninResponse(Status.WRONG_CREDENTIALS, remainingAttempts);
    }

    public static SigninResponse wrongCredentials() {
        return new SigninResponse(Status.WRONG_CREDENTIALS, -1);
    }

    public static SigninResponse softLocked(int timeOut) {
        return new SigninResponse(Status.SOFT_LOCKED, timeOut);
    }

    public static SigninResponse hardLocked() {
        return new SigninResponse(Status.HARD_LOCKED, -1);
    }

    public static SigninResponse passwordReset(String encryptedEmail) {
        SigninResponse response = new SigninResponse(Status.PASSWORD_RESET, -1);
        response.additionalData = encryptedEmail;
        return response;
    }

    public static SigninResponse unexpectedFailure() {
        return new SigninResponse(Status.UNEXPECTED_FAILURE, -1);
    }

    public static SigninResponse generalFailure() {
        return new SigninResponse(Status.OTHER_FAILURE, -1);
    }

    public String getAdditionalData() {
        return additionalData;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public Status getStatus() {
        return status;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public enum Status {
        SUCCESS, WRONG_CREDENTIALS, SOFT_LOCKED, HARD_LOCKED, PASSWORD_RESET, OTHER_FAILURE, UNEXPECTED_FAILURE
    }
}
