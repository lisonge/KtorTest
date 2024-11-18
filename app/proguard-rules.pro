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

# Uncomment this to preserve the line number information for debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to hide the original source file name.
-renamesourcefileattribute SourceFile

# self
-keep class ktor.test.**{*;}
-keep interface ktor.test.**{*;}

#-keep class coil.**{*;}
#-keep class com.hjq.toast.** {*;}

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

-keep, allowobfuscation, allowoptimization @kotlinx.serialization.Serializable class * {*;}

## Android architecture components: Lifecycle
# LifecycleObserver's empty constructor is considered to be unused by proguard
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <init>(...);
}
# ViewModel's empty constructor is considered to be unused by proguard
-keepclassmembers class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
# keep methods annotated with @OnLifecycleEvent even if they seem to be unused
# (Mostly for LiveData.LifecycleBoundObserver.onStateChange(), but who knows)
-keepclassmembers class * {
    @androidx.lifecycle.OnLifecycleEvent *;
}

# https://github.com/ktorio/ktor/issues/379
-keep class kotlin.reflect.jvm.internal.** { *; }

# https://github.com/ktorio/ktor-documentation/blob/2.3.6/codeSnippets/snippets/proguard/proguard.pro
-keep class io.ktor.server.cio.EngineMain { *; }
-keep class io.ktor.server.config.HoconConfigLoader { *; }

# kotlin
-keep class kotlin.** { *; }
-keep class kotlin.Metadata { *; }
-keep class kotlin.text.RegexOption { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

# gradle build error log
-dontwarn java.lang.management.ManagementFactory**
-dontwarn java.lang.management.RuntimeMXBean**
-dontwarn org.slf4j.impl.StaticLoggerBinder**
-dontwarn org.slf4j.impl.StaticMDCBinder**

# <kotlinx.coroutines-
# https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-core/jvm/resources/META-INF/proguard/coroutines.pro
# When editing this file, update the following files as well:
# - META-INF/com.android.tools/proguard/coroutines.pro
# - META-INF/com.android.tools/r8/coroutines.pro
-keep class kotlinx.coroutines.** {*;}

# ServiceLoader support
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepnames class kotlinx.coroutines.android.AndroidExceptionPreHandler {}
-keepnames class kotlinx.coroutines.android.AndroidDispatcherFactory {}

# Most of volatile fields are updated with AFU and should not be mangled
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}

# Same story for the standard library's SafeContinuation that also uses AtomicReferenceFieldUpdater
-keepclassmembers class kotlin.coroutines.SafeContinuation {
    volatile <fields>;
}

# Only used in `kotlinx.coroutines.internal.ExceptionsConstructor`.
# The case when it is not available is hidden in a `try`-`catch`, as well as a check for Android.
-dontwarn java.lang.ClassValue
# -kotlinx.coroutines>
