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
#retrofit:2.4.0
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain service method parameters.
-keepclassmembernames,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}
# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# OkHttp3
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn okhttp3.logging.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-keep class okhttp3.internal.**{*;}

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
-keep class com.kmjd.android.zxhzm.alipaybill.bean.** { *; }

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



