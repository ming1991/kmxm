# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\android-sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
## normal
#由于proguard-android.txt文件中已经指定了一部分规则，以下为自定义规则

# 混淆时采用的算法(google推荐，一般不改变)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!class/unboxing/enum,!code/allocation/variable,!method/marking/private
-optimizationpasses 5# 指定代码的压缩次数，如果第一次失败，则不会继续执行
-keep class com.jph.android.entity.** { *; }# 不混淆实体类
-keep class com.jph.android.view.** { *; }#自定义控件不参与混淆
-keepattributes SourceFile,LineNumberTable# 保留行号
-keep class * implements android.os.Parcelable {#保护实现了Parcelable接口的类
public static final android.os.Parcelable$Creator *;
}
-mergeinterfacesaggressively# 合并接口，在特定阶段有效
-keepattributes Exceptions# 异常
-keepattributes Signature# 泛型
-keepattributes EnclosingMethod# 反射
-dontwarn android.support.**# 忽略support包警告
#-dontskipnonpubliclibraryclasses# 不忽略非公共的库类
-dontusemixedcaseclassnames# 是否使用大小写混合
-dontpreverify# 混淆时不做预校验
-verbose# 混淆时是否记录日志
-keepattributes *Annotation*# 保持注解
-ignorewarning# 忽略警告
-dontoptimize# 不优化输入的类文件
#保持哪些类不被混淆
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
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
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-dontwarn com.google.gson.**
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keepattributes EnclosingMethod
-keep class com.google.gson.** {*;}
-keep class com.kmjd.wcqp.single.zxh.model.** { *; }
#友盟统计混淆规则
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class [com.kmjd.wcqp.single.zxh].R$*{
public static final int *;
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
#rxjava
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
#阿里巴巴fastJson
-keepattributes Signature
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.**{*; }
#-------------------------------------------------------------------------------------------------
#抓包包下全部不混淆
-keep class net.**{*;}
#生成日志数据，gradle build时在本项目根目录输出
#-libraryjars libs/netty-android.jar#混淆第三方jar包，其中xxx为jar包名
-keep class net.lightbody.bmp.** {*;}
-keep class com.netease.** {*;}
-keep class jniLibs.**{*;}
-dump class_files.txt#apk包内所有class的内部结构
-printseeds seeds.txt#未混淆的类和成员
-printusage unused.txt#打印未被使用的代码
-printmapping mapping.txt#混淆前后的映射
#-keep class com.xxx.**{*;}       #不混淆某个包内的所有文件
#-dontwarn com.xxx**              #忽略某个包的警告
-keepnames class * implements java.io.Serializable #不混淆Serializable
-keep class * implements android.os.Parcelable {# 保持 Parcelable 不被混淆
    public static final android.os.Parcelable$Creator *;
}
-keepclassmembers class **.R$* { #不混淆资源类
    public static <fields>;
}
-keepclasseswithmembernames class * {# 保持 native 方法不被混淆
    native <methods>;
}

-keepclassmembers enum * {# 保持枚举 enum 类不被混淆
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# Bugly
-dontwarn com.tencent.bugly.**
-keep class com.tencent.bugly.** {*;}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
# Jackson
-dontwarn org.codehaus.jackson.**
-dontwarn com.fasterxml.jackson.databind.**
-keep class org.codehaus.jackson.** { *;}
-keep class com.fasterxml.jackson.** { *; }

#EventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends org.greenrobot.eventbus.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}



