# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile



-keepclassmembers class suncor.com.android.** {
    public *;
    protected *;
}
-keepclassmembers class com.mazenrashed.logdnaandroidclient.** {
    public *;
    protected *;
}

####################################################################################################
##############################  IBM MobileFirst Platform configuration  ############################
####################################################################################################
# Annotations are represented by attributes that have no direct effect on the execution of the code.
-keepattributes *Annotation*

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepattributes InnerClasses
-keep class **.R
-keep class **.R$* {
    <fields>;
}
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable
-keep class com.worklight.androidgap.push.** { *; }
-keep class com.worklight.wlclient.push.** { *; }
-keep class com.worklight.common.security.AppAuthenticityToken { *; }
####################################################################################################

#OkHttp
-dontwarn okio.**
-dontwarn com.squareup.okhttp.**
-ignorewarnings

#################

## Keep all the model class
-keep class suncor.com.android.model.**{ *; }

## Keep all the enums
-keepclasseswithmembers enum suncor.com.android.**{ *; }

## Keep all the parcelized classes
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

## keep android classes
-keep class androidx.** { *; }