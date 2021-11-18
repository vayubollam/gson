package suncor.com.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Locale;

public class SettingsResponse {
    private Settings settings;
    private BuildInfo buildInfo;

    public Settings getSettings() {
        return settings;
    }

    public BuildInfo getBuildInfo() {
        return buildInfo;
    }

    public static class Settings {
        private String dt;
        private String currentAndroidVersion;
        private String minAndroidVersion;
        private String currentIOSVersion;
        private String minIOSVersion;
        private String descriptionEN;
        private String descriptionFR;
        private Carwash carwash;
        @SerializedName("payatpump")
        private Pap pap;
        private String maintenanceMsgFR;
        private String maintenanceMsgEN;
        private GooglePassConfig googlePass;

        public String getDescriptionEN() {
            return descriptionEN;
        }

        public String getDescriptionFR() {
            return descriptionFR;
        }

        public String getDt() {
            return dt;
        }

        public String getCurrentAndroidVersion() {
            return currentAndroidVersion;
        }

        public String getMinAndroidVersion() {
            return minAndroidVersion;
        }

        public Carwash getCarwash() {
            return carwash;
        }

        public Pap getPap() {
            return pap;
        }

        public String getMaintenanceMsgFR() {
            return maintenanceMsgFR;
        }

        public String getMaintenanceMsgEN() {
            return maintenanceMsgEN;
        }

        public String getMaintenanceDisplayMsg() {
           return Locale.getDefault().getDisplayLanguage().equals("English") ? maintenanceMsgEN : maintenanceMsgFR;
        }

        public GooglePassConfig getGooglePass() {
            return googlePass;
        }
    }


    public static class BuildInfo {
        private String environment;
        private String gitHash;
        private String buildTime;
        private String version;
        private String buildNumber;

        public String getEnvironment() {
            return environment;
        }

        public String getGitHash() {
            return gitHash;
        }

        public String getBuildTime() {
            return buildTime;
        }

        public String getVersion() {
            return version;
        }

        public String getBuildNumber() {
            return buildNumber;
        }
    }

    public static class Carwash {
        private String key;

        public String getKey() {
            return key;
        }
    }

    public static class Pap {
        private int otherAmountHighLimit;
        private int otherAmountLowLimit;
        private int geofenceDistanceMeters;
        private HashMap<String,String> preAuthLimits;
        private String p97TenantID;

        public int getGeofenceDistanceMeters() {
            return geofenceDistanceMeters;
        }

        public int getOtherAmountHighLimit() {
            return otherAmountHighLimit;
        }

        public int getOtherAmountLowLimit() {
            return otherAmountLowLimit;
        }

        public HashMap<String, String> getPreAuthLimits() {
            return preAuthLimits;
        }

        public String getP97TenantID() {
            return p97TenantID;
        }
    }

    public static class GooglePassConfig{
        private String googlePassesClassId;
        private String googlePassesIssuerId;
        private String googlePassesAccountEmailAddress;

        public String getGooglePassesClassId() {
            return googlePassesClassId;
        }

        public String getGooglePassesIssuerId() {
            return googlePassesIssuerId;
        }

        public String getGooglePassesAccountEmailAddress() {
            return googlePassesAccountEmailAddress;
        }

    }
}

