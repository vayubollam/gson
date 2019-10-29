package suncor.com.android.model;

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
}

