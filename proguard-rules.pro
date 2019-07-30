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
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-dontshrink#混淆jar的时候一定要配置，不然会把没有用到的代码全部remove   我本来封装一个jar就是给别人调用的，全部删掉就没有东西了
-verbose

-keepattributes Signature #过滤泛型 用到发射，泛型等要添加这个
-keepattributes *Annotation*

-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#如果你使用了ProGuard来导入第三方jar这个地方就不用配置了
#-libraryjars ../meddo/libs/android.jar
#-libraryjars ../meddo/libs/gson-2.5.jar
#-libraryjars ../meddo/libs/okhttp-2.5.0.jar
#-libraryjars ../meddo/libs/okio-1.6.0.jar

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#解析数据是用的的bean  完全不混淆，不然解析json数据时什么都找不到
-keep public class com.eryanet.m85musicsdk.bean.*{*;}

#不要混淆AudioSDKCallback的public方法
-keep public class com.eryanet.m85musicsdk.inter.MusicListCallBack{
 public <methods>;
}
-keep public class com.eryanet.m85musicsdk.inter.MusicPlayerCallBack{
 public <methods>;
}

-keep public class com.eryanet.m85musicsdk.sdk.M85MusicSDK{
 *;
}
#-keep public class com.eryanet.audiosdk.utils.RequestUtil{
# *;
#}
#-keep public class com.eryanet.audiosdk.tools.AccessTokenManager{
# public <methods>;
#}

#-dontwarn javax.annotation.**
#-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase
#-dontwarn org.codehaus.mojo.animal_sniffer.*
#-dontwarn okhttp3.internal.platform.ConscryptPlatform

#okhttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *;}
-keep interface okhttp3.** { *; }

-dontwarn okio.**
#-keep class okio.** {*;}
#-keep class okhttp3.**{ *;}
#-keep class okhttp3.internal.**{ *;}
#-keep class okhttp3.internal.cache.** { *;}
#-keep class okhttp3.internal.cache2.** { *;}
#-keep class okhttp3.internal.connection.** { *;}
#-keep class okhttp3.internal.http.** { *;}
#-keep class okhttp3.internal.http1.** { *;}
#-keep class okhttp3.internal.http2.** { *;}
#-keep class okhttp3.internal.io.** { *;}
#-keep class okhttp3.internal.platform.** { *;}
#-keep class okhttp3.internal.publicsuffix.** { *;}
#-keep class okhttp3.internal.tls.** { *;}
#-keep class okhttp3.internal.ws.** { *;}

#json
#-dontwarn com.google.gson.**
#-keep class com.google.gson.**{*;}

-dontwarn com.google.gson**
-keep class com.google.gson** { *;}
