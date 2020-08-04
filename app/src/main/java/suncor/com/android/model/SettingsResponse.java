package suncor.com.android.model;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;

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
        private HashMap<String,Integer> preAuthLimits;

        public int getGeofenceDistanceMeters() {
            return geofenceDistanceMeters;
        }

        public int getOtherAmountHighLimit() {
            return otherAmountHighLimit;
        }

        public int getOtherAmountLowLimit() {
            return otherAmountLowLimit;
        }

        public HashMap<String, Integer> getPreAuthLimits() {
            return preAuthLimits;
        }

        /*  public Limits getPreAuthLimits() {
            return preAuthLimits;
        }

        public static class Limits {
            @SerializedName("1")
            private int limit1;
            @SerializedName("2")
            private int limit2;
            @SerializedName("3")
            private int limit3;
            @SerializedName("4")
            private int limit4;

            public int getLimit1() {
                return limit1;
            }

            public int getLimit2() {
                return limit2;
            }

            public int getLimit3() {
                return limit3;
            }

            public int getLimit4() {
                return limit4;
            }
        }*/
    }
}

