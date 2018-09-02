# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
## normal
#由于proguard-android.txt文件中已经指定了一部分规则，以下为自定义规则
# 混淆时采用的算法(google推荐，一般不改变)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!class/unboxing/enum,!code/allocation/variable,!method/marking/private
# 指定代码的压缩次数，如果第一次失败，则不会继续执行
-optimizationpasses 5
# 不混淆实体类
-keep class com.jph.android.entity.** { *; }
#自定义控件不参与混淆
-keep class com.jph.android.view.** { *; }
# 保留行号
-keepattributes SourceFile,LineNumberTable
#保护实现了Parcelable接口的类
-keep class * implements android.os.Parcelable {public static final android.os.Parcelable$Creator *;}
# 合并接口，在特定阶段有效
-mergeinterfacesaggressively
# 异常
-keepattributes Exceptions
# 泛型
-keepattributes Signature
# 反射
-keepattributes EnclosingMethod
# 忽略support包警告
-dontwarn android.support.**

#如果引用了v4或者v7包
# Keep the support library
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# Keep the support library
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
#design包
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

#ButterKnife混淆规则
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

#Retrofit2混淆规则
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions

# OkHttp3
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn okhttp3.logging.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okhttp3.internal.**{*;}

# Gson
-dontwarn com.google.gson.**
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keepattributes EnclosingMethod
-keep class com.google.gson.** {*;}
-keep class com.md.jsyxzs_cn.zym_xs.network.nw_respone_model.** { *; }
-keep class com.md.jsyxzs_cn.zym_xs.network.nw_respone_ts_model.** { *; }

#友盟统计混淆规则
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class [com.md.jsyxzs_cn.zym_xs].R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

