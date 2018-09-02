# 混淆时采用的算法(google推荐，一般不改变)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!class/unboxing/enum,!code/allocation/variable,!method/marking/private
-optimizationpasses 7# 指定代码的压缩次数，如果第一次失败，则不会继续执行
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